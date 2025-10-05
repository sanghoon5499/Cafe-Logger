# CafeLogger
CafeLogger is an Android app for coffee nerds (like your truly) to track their cafe experiences and remember their favorite beans. Never again forget the details of that amazing pour-over or the cozy vibe of a new cafe. This app allows you to log your coffee journey.

# Features
Features
- **Log Entries**: Create detailed logs for coffee beans or drinks.
- **Photo Capture**: Add a photo to each entry.
- **Detailed Fields**: Record details like origin, roast level, processing method, and drink style.
- **Home Feed**: View all your recent entries in a clean two-column grid.
- **Details View**: Tap on any entry to see its full details on a dedicated screen

# Tech Stack
- **UI**: Jetpack Compose, Navigation
- **Architecture**: MVVM
- **State Management**: StateFlow and MutableStateFlow (ViewModel <--> View)
- **Async ops**: Kotlin coroutines (lazy, suspend fun)
- **DI**: ViewModel Factory manual injection
- **Images**: Coil

# Future Feature Additions
- Google Maps/Places API for nearby cafe search, as well as location search when adding a new drink/bean entry
- Rating system

# Tech Talk
This is the second iteration of CafeLogger. The original version was built using the traditional Android View system (Activities, Fragments, XML). While functional, I found that managing the UI with XML became too complex and time-consuming. The constant back-and-forth between layout files and business logic made development slow and difficult to maintain.

I used Figma for the app's initial designs:
![Old design](https://storage.googleapis.com/readme_photos/OLD_DESIGN.png)

This inspired a complete rewrite using Jetpack Compose. This modern approach simplified the UI layer, and the modular nature of Composable functions made it easy to create reusable and testable components. From learning about the modularization of Compose components, to a deeper dive into ViewModels with Factories, this project was meant to build upon what I already knew about starting up an Android app from scratch, and into something more complicated. 

The "Find a cafe" button on the old design did actually exist on the first iteration of the app, but with the focus becoming more on MVVM and Compose, I decided to be code-quality first, and feature complete next. Of course, I don't plan on ditching the whole Google Maps aspect entirely. Having played around with the Maps/Places API, it's a much larger fish than what I think I can handle right now.

The new design is similar when it comes to UI and UX, but the code behind the logic has been simplified greatly. Like I mentioned earlier, UI with XML gets unsightly real fast, so Compose was such a nice improvement.

Here's the new design (live build on my phone):
![Old design](https://storage.googleapis.com/readme_photos/NEW%20DESIGN.png)

I still plan on getting back to the Maps/Places API someday, but perhaps after I can work through exactly what I want to do with it. 

# Installation
This project does not have a public distribution. Please contact me at `sanghoon5499@gmail.com` for access to the latest APK for testing.

## Android Build Info
* `compileSdk = 36`
* `AGP Version: 8.9.1`
