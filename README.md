# Convenience Store Stock Management Application

## Setup instructions

### Prerequisites
- **JDK 17** or higher.
- **Android Studio Jellyfish (2023.3.1)** or newer.
- **Android SDK 34** (compileSdk and targetSdk).

### Getting Started
1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd ConvenienceStoreAssessment
   ```

2. **Open the project**:
   Open Android Studio and select **File > Open**, then choose the `ConvenienceStoreAssessment` directory.

3. **Sync Gradle**:
   Wait for Android Studio to finish the Gradle sync process. If prompted, allow it to download necessary dependencies.

4. **Build the project**:
   You can build the project from the IDE or using the terminal:
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run the application**:
   - Select the `app` configuration in the toolbar.
   - Choose an emulator or a physical device with at least **Android 7.0 (API level 24)**.
   - Click the **Run** button (green arrow).

## Features
- Authentication
- Products
  - Add product
  - Edit product
  - Delete product
  - List products
- Suppliers
- Stocks
  - Add stocks
  - Remove stocks
- Dashboard

## Architecture Overview

Application is organized in modules, each feature has its own set of modules. 
The project follows **Clean Architecture** principles, separating concerns into three layers per feature:
- **:domain**: Contains entities, repository interfaces, and use cases. Pure Kotlin module.
- **:data**: Implementation of repositories, data sources (Room/Retrofit), and models.
- **:ui**: Jetpack Compose screens, ViewModels, and UI-specific components.

## Best practices

### Resources
Resources are defined within each module. To prevent naming conflicts during resource merging, 
each resource key must start with the module name as a prefix (e.g., `core_action_save`).

### Dependency Injection
The project uses **Hilt** for dependency injection. Each module should have its own Hilt module for providing its internal dependencies.

## Known issues or limitations
- Category and Supplier selection in "Add Product" currently uses mock data and needs to be connected to real data sources.
- Search functionality in the products list is not yet fully implemented.
