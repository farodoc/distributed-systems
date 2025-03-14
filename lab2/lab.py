from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI()

class Answer(BaseModel):
    text: str
    votes: int = 0

class Poll(BaseModel):
    name: str
    answers: list[Answer] = []

current_polls: list[Poll] = []

@app.get("/poll")
async def get_polls():
    return current_polls

@app.get("/poll/{poll_id}")
async def get_poll(poll_id: int):
    if poll_id < 0 or poll_id >= len(current_polls):
        raise HTTPException(status_code=404, detail="Poll not found")
    return current_polls[poll_id]

@app.post("/poll")
async def create_poll(poll: Poll):
    current_polls.append(poll)
    return {"id": len(current_polls) - 1, "poll": poll}

@app.put("/poll/{poll_id}/vote")
async def cast_vote(poll_id: int, answer_id: int):
    if poll_id < 0 or poll_id >= len(current_polls):
        raise HTTPException(status_code=404, detail="Poll not found")
    if answer_id < 0 or answer_id >= len(current_polls[poll_id].answers):
        raise HTTPException(status_code=404, detail="Answer not found")
    
    current_polls[poll_id].answers[answer_id].votes += 1
    return current_polls[poll_id].answers[answer_id]

@app.delete("/poll/{poll_id}")
async def delete_poll(poll_id: int):
    if poll_id < 0 or poll_id >= len(current_polls):
        raise HTTPException(status_code=404, detail="Poll not found")
    
    deleted_poll = current_polls.pop(poll_id)
    return {"message": f"Poll '{deleted_poll.name}' deleted successfully"}
