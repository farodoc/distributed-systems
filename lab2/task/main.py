from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import HTMLResponse
from http import HTTPStatus
import httpx
from datetime import datetime
from typing import Optional, List

app = FastAPI()

WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast"
GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json"
GOOGLE_API_KEY = "AIzaSyBqlV7Xk-ifzvAdkKnyv6c9DjORCIEq6tk"


@app.get("/")
async def root():
    with open("index.html") as file:
        return HTMLResponse(content=file.read(), status_code=HTTPStatus.OK)

    
@app.get("/weather")
async def get_weather(
    city: Optional[str] = None,
    latitude: Optional[float] = None,
    longitude: Optional[float] = None,
    weather_params: List[str] = Query(default=["temperature_2m"])
):
    try:
        if city and not (latitude and longitude):
            async with httpx.AsyncClient() as client:
                params = {
                    "address": city,
                    "key": GOOGLE_API_KEY
                }

                geo_response = await client.get(GEOCODING_API_URL, params=params)
                geo_response.raise_for_status()
                geo_data = geo_response.json()

                if geo_data["status"] == "OK":
                    location = geo_data["results"][0]["geometry"]["location"]
                    latitude = location["lat"]
                    longitude = location["lng"]
                else:
                    raise HTTPException(status_code=HTTPStatus.NOT_FOUND, detail="City not found")
        
        if not (latitude and longitude):
            raise HTTPException(status_code=HTTPStatus.BAD_REQUEST, detail="Either city name or both latitude and longitude are required")

        async with httpx.AsyncClient() as client:
            params = {
                "latitude": latitude,
                "longitude": longitude,
                "hourly": ",".join(weather_params)
            }

            response = await client.get(WEATHER_API_URL, params=params)
            response.raise_for_status()
            weather_data = response.json()

        results = generate_results(weather_data, city, latitude, longitude, weather_params)
        
        return HTMLResponse(content=results, status_code=HTTPStatus.OK)
            
    except Exception as e:
        raise HTTPException(status_code=HTTPStatus.INTERNAL_SERVER_ERROR, detail=str(e))

    
def generate_results(weather_data, city, latitude, longitude, weather_params):
    times = weather_data['hourly']['time'][:24]   
    location_name = city if city else f"({latitude}, {longitude})"
    
    param_names = {
        "temperature_2m": "Temperature (°C)",
        "relative_humidity_2m": "Relative Humidity (%)",
        "rain": "Rain (mm)",
        "snowfall": "Snowfall (cm)"
    }

    html_content = f"""
    <html>
        <head>
            <title>Weather Forecast</title>
        </head>
        <body>
            <h1>Weather Forecast for {location_name}</h1>
            <table>
                <tr>
                    <th>Time</th>
    """

    for param in weather_params:
        html_content += f"<th>{param_names.get(param, param)}</th>"
    
    html_content += "</tr>"

    for i, time in enumerate(times):
        html_content += f"""
                <tr>
                    <td>{time}</td>
        """

        for param in weather_params:
            value = weather_data['hourly'][param][i]
            html_content += f"<td>{value}</td>"

        html_content += "</tr>"
    
    html_content += """
            </table>
            <br>
            <a href="/">Back to search</a>
        </body>
    </html>
    """
    
    return html_content