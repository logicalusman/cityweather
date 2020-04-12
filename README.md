# cityweather
Fetches basic weather data of any city in the world by using openweathermap.org api.

## Why this project?
This project is a demonstration of using coroutines alongside viewmodel and livedata. Also it covers testing of coroutines':
1. `suspend` functions
2. coroutine builders, such as `launch`

### Architecture and overall structure
Architecture is MVVM based.

Uses repositories as a gateway to fetch data from datasource.

No dependency injection framework is used to keep things simple, however, dependencies are manually generated and injected via a single place.

Concurrency is handled via coroutines. Uses retrofit for web api.

Uses `mockk` and `junit` for unit testing
