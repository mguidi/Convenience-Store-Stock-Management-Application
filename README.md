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
- Suppliers (not implemented)
- Stocks
  - Add stocks
  - Remove stocks
- Scanner
- Dashboard

## Architecture Overview

Application is organized in modules, each feature has its own set of modules. 
The project follows **Clean Architecture** principles, separating concerns into three layers per feature:
- **:domain**: Contains entities, repository interfaces, and use cases. Pure Kotlin module.
- **:data**: Implementation of repositories, data sources (Room/Ktor), and models.
- **:ui**: Jetpack Compose screens, ViewModels, and UI-specific components.

### Modular Interaction Architecture
In this system, communication between independent modules is governed by a decoupled architecture, 
ensuring that components remain isolated yet collaborative. This is achieved through two primary mechanisms:

#### **1. Interfaces and Use Cases**:

Interaction is strictly regulated through defined Interfaces and Use Cases.

   - Interfaces act as the contract, defining what a module can do without exposing how it does it.

   - Use Cases encapsulate specific business logic, allowing modules to trigger cross-functional

   - workflows in a predictable and testable manner.

#### **2. Centralized Event-Driven Communication**:

To facilitate asynchronous interaction and system-wide visibility, we utilize a centralized 
Event Log located within the *core:data* module.

   - Unified Tracking: Every module has the capability to persist its own domain-specific events 
   (e.g., ProductCreateEvent, StockAddEvent, StockRemoveEvent) into this shared table.
   
   - Observability: Because the event_log is hosted in the core layer, it serves as a 
   "Single Source of Truth." Any module can observe these events to trigger its own internal logic
   without requiring a direct dependency on the originating module.


## Data Synchronization (Offline-First)

The application implements an **Offline-First** strategy, ensuring a seamless user experience even 
without an active internet connection. The local database (Room) acts as the **Single Source of Truth (SSOT)** 
for all UI components.

### 1. Outbound Sync (Writing Data)
When a user performs an action (e.g., adding stock), the application follows a reactive event-driven flow:
- **Local Persistence:** The operation is immediately saved to the local database and an entry is created in the `event_log`.
- **Background Worker:** A `WorkManager` task periodically (or upon event trigger) reads the unsynchronized events from the `event_log` and 
sends the data to the server. Once the server acknowledges the request, the event is marked as "synchronized" in the local DB.

### 2. Inbound Sync (Reading Data)
To ensure maximum responsiveness and data consistency, the **Repository** acts as the orchestrator for all data requests using a **Resource-bound strategy**:

* **Local Emission**: The Repository immediately fetches the cached data from the Room database and emits it to the UI (via *Flow*).
* **Remote Fetch**: Simultaneously, it triggers a network request to the server API to fetch the most recent data.
* **Persistence**: Upon a successful response, the Repository processes and saves the new data into the local database, overwriting or updating the stale cache.
* **Final Emission**: Once the database is updated, the Repository performs a fresh read from the local storage and emits the latest "truth" to the UI.

This approach ensures the user sees data immediately while the Repository handles the complexity of 
synchronizing the local state with the remote server in the background.

> **Benefit:** This "Cache-then-Network" pattern eliminates loading spinners for previously cached data and allows the app to function perfectly in 
"Dead Zones" (elevators, basements, etc.).

## Database Architecture & Data Integrity

The application utilizes **Room** as its persistent storage engine. While the project is modular, 
we have opted for a **Single Shared Database** approach rather than separate databases per module. 
This decision is based on two primary factors:

* **Performance & Resource Efficiency**: Running multiple database instances on Android is resource-intensive 
and can lead to unnecessary memory overhead.

* **Atomic Transactions**: A shared database allows us to perform write operations (e.g., updating stock) and 
record the corresponding event in the `event_log` table within a **single atomic transaction**. 
This ensures data consistency and prevents desynchronization between state changes and their audit logs.

#### Enforcement of Module Boundaries
Although the database is technically shared, we enforce strict access rules to prevent tight coupling:
* **Encapsulation**: Each module is restricted to accessing only its own tables.

* **Dependency Management**: Accessing another module's table directly is strictly prohibited. 
If a module needs data from another domain, it must do so via the exposed **Interfaces** or **Use Cases**, 
ensuring that modules remain decoupled from each other's underlying data schemas.

## Best practices

### Resources
Resources are defined within each module. To prevent naming conflicts during resource merging, 
each resource key must start with the module name as a prefix (e.g., `core_action_save`).

### Dependency Injection
The project uses **Hilt** for dependency injection. Each module should have its own Hilt module for providing its internal dependencies.

## Known issues or limitations

### Offline Inventory Consistency

While the **Offline-First** approach ensures high availability, it introduces a specific limitation regarding **Real-time Stock Accuracy**:

* **Distributed State Conflict**: Since operators work on local data, an operator might perform a 
"Remove Stock" action based on a local quantity that has already been depleted by another user on the server.

* **Delayed Validation**: Because synchronization is asynchronous (handled by the `SyncWorker`), 
the server-side validation happens only *after* the local action has been completed.

* **Risk of Negative Inventory**: This can lead to temporary discrepancies where the local app shows 
available stock that does not physically exist, potentially resulting in "Negative Stock" errors once 
the sync is processed by the server. (In this example I accept negative stock).

