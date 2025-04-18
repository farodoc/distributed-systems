# Zadanie A1 - "Inteligentny" dom
Aplikacja ma pozwalać na zdalne zarządzanie urządzeniami tzw. inteligentnego domu, którego wyposażeniem są różne urządzenia, np. czujniki czadu czy zdalnie sterowane lodówki, piece, kamery monitoringu z opcją PTZ, bulbulatory, itp. Każde z urządzeń może występować w kilku nieznacznie się różniących odmianach, a każda z nich w pewnej (niewielkiej) liczbie instancji. Dom ten nie oferuje obecnie możliwości budowania złożonych układów, pozwala użytkownikom jedynie na zdalne sterowanie pojedynczymi urządzeniami oraz odczytywanie ich stanu.

Dodatkowe informacje i wymagania:

- Każde z urządzeń inteligentnego domu jest reprezentowane przez obiekt/usługę strony serwerowej. Sposób jego integracji i komunikacji z rzeczywistym, sterowanym urządzeniem nie jest przedmiotem zainteresowania projektu. Urządzenia mogą działać na wielu instancjach serwerów (demonstracja: na co najmniej dwóch).

- Projektując interfejs IDL urządzeń należy używać także typów bardziej złożonych niż string czy int/long (tj. struktury, sekwencje itp.). Trzeba pamiętać o deklaracji i zgłaszaniu wyjątków lub błędów tam, gdzie to może mieć zastosowanie.
Wystarczająca jest obsługa dwóch-trzech typów urządzeń, jeden-dwa z nich mają mieć dwa-trzy podtypy. 

- Należy odwzorować podane wymagania do cech wybranej technologii w taki sposób, by jak najlepiej wykorzystać oferowane przez nią możliwości budowy takiej aplikacji i by osiągnąć jak najbardziej eleganckie rozwiązanie (gdyby żądanej funkcjonalności nie dało się wprost osiągnąć). Decyzje projektowe trzeba umieć uzasadnić.

- Zestaw urządzeń może być niezmienny w czasie życia serwera (tj. dodanie nowego urządzenia może wymagać modyfikacji kodu serwera i restartu procesu). Aplikacja kliencka może być świadoma obsługiwanych typów urządzeń w czasie kompilacji.

- Początkowy stan instancji obsługiwanego urządzenia może być zawarty w kodzie źródłowym strony serwerowej lub pliku konfiguracyjnym.

- Aplikacja kliencka ma pozwalać zademonstrować sterowanie wszystkimi urządzeniami bez konieczności restartu w celu przełączenia na inne urządzenie.

- Serwer może zapewnić funkcjonalność wylistowania nazw (identyfikatorów) aktualnie dostępnych instancji urządzeń.

- Dla chętnych: wielowątkowość strony serwerowej.

- Dla chętnych: wzbogacenie systemu o reverse-proxy (gRPC).
