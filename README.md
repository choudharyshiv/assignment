# MatchMate

A Matrimonial Android app that displays match cards, allowing users to accept or decline matches. Built with MVVM, Clean Architecture, and best-in-class libraries.

## Features
- Fetches user data from [randomuser.me](https://randomuser.me/api/?results=10) with pagination
- Displays match cards with user image, details, and Accept/Decline buttons
- Stores user decisions (accept/decline) in a local Room database
- Works seamlessly offline; syncs when network is available
- Error handling and offline indicators
- Modern UI with RecyclerView and Paging 3

## Architecture
- **MVVM**: Separation of concerns for maintainability
- **Clean Architecture**: Data, Domain, Presentation layers
- **Repository Pattern**: Abstracts data sources

## Libraries Used
- [Retrofit](https://square.github.io/retrofit/) - API calls
- [Room](https://developer.android.com/jetpack/androidx/releases/room) - Local database
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) - Pagination
- [Glide](https://github.com/bumptech/glide) - Image loading
- [LiveData/StateFlow](https://developer.android.com/topic/libraries/architecture/livedata) - Data flow

## Setup Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle and build the project
4. Run on an emulator or device (minSdk 24)

## Usage
- Browse match cards
- Accept or decline matches; status is shown on each card
- Works offline; decisions are stored and synced when online

## Special Notes
- All data is cached locally for offline use
- Error and network status are shown via Toasts/Snackbars

## Folder Structure
- `data/` - API, Room, Repository
- `domain/` - Models, UseCases
- `presentation/` - ViewModel, UI

## License
MIT

