# Inventory Management System

A Java Swing desktop application developed as coursework for the Data Structures and Algorithms course. It manages inventory, stock-in, and stock-out records with custom-implemented data structures.

## Important Notice

This project is developed for ACADEMIC PURPOSES ONLY. It is NOT production-ready and should NOT be used in real-world environments. No warranty is provided.

## Project Overview

This inventory management system is designed for personal learning and academic demonstration. It implements practical warehouse operations including batch stock-in and batch price modification, with a focus on applying data structures to solve real-world problems.

**Course:** Data Structures and Algorithms

**Grade:** A+ (94/100)

## Key Features

1. Inventory Management (CRUD operations for products)
2. Batch Stock-in Processing
3. Batch Price Modification
4. Batch Stock-out Processing
5. Data Analysis and Stock Prediction
6. Data Search and Filter
7. Graphical User Interface (Java Swing, English-only)
8. Native Window Controls (minimize/maximize/close)

## Data Structures Implemented

This project is built around custom implementations of the following data structures with no external library imports:

### Sequential List (Used for Inventory Storage)

- Applied to inventory data for efficient sequential access and fast random queries
- O(1) time complexity for get/set operations by index

### Linked List (Used for Stock Records)

- Applied to stock-in and stock-out records for frequent insertions and deletions
- O(1) time complexity for node insertions and deletions at known positions

### Design Rationale

- **Sequential List for Inventory:** Products are relatively stable, requiring fast lookup by position. Sequential list provides cache-friendly memory layout and O(1) random access.
- **Linked List for Records:** Stock-in/out records are frequently added, modified, and removed. Linked list provides O(1) insertion and deletion after locating the node, avoiding array shifting overhead.

## Technical Stack

- **Language:** Java 8+
- **GUI:** Java Swing (JFrame, JTable, JTabbedPane)
- **Window Controls:** Native title bar (minimize/maximize/close)
- **Data Format:** JSON (custom parser, no external dependencies)
- **Data Source:** Local JSON files (English product names)
- **Build:** javac (no build tools required)
- **Language Support:** English-only UI and code comments

## Development Process

### Challenges Encountered

1. **Data Structure Selection:** Determining which data structure fits each business scenario (static inventory vs. dynamic records)
2. **UI Design:** Creating an intuitive and visually appealing Swing interface with proper layout management
3. **Data Relationships:** Managing complex relationships between inventory, stock-in, and stock-out data
4. **AI Collaboration:** Learning to effectively communicate requirements to AI agents for optimal code generation

### Skills Acquired

- AI Collaboration techniques for rapid prototyping
- Data structure selection based on usage patterns
- System architecture and design thinking
- Effective AI-agent collaboration for software development

### Developer Responsibilities

- System architecture and design decisions
- Data structure selection and algorithm design
- Business logic and requirements definition
- UI/UX design for the Swing interface
- Code review and quality assurance

### AI Agent Responsibilities

- Code generation and refactoring
- Debugging and error fixing
- Data structure implementation guidance
- Data migration from API to local JSON
- Repetitive code automation

## Project Structure

    src/
      zyhinventory/
        ZYHInventoryApp.java       Main application entry point, GUI framework
        ZYHDataManager.java        Data orchestration and business logic layer
        ZYHApiClient.java          Remote API client (commented out, local mode active)
        ZYHLocalData.java          Local JSON data reader for offline mode
        ZYHSequentialList.java     Sequential list implementation (inventory storage)
        ZYHLinkedList.java         Linked list implementation (stock records)
        ZYHProduct.java            Product data model
        ZYHInboundRecord.java      Stock-in record data model
        ZYHOutboundRecord.java     Stock-out record data model
        ZYHTransactionRecord.java  Transaction record data model
        ZYHFilterDialog.java       Filter dialog with multi-condition search
    data/
      inventory.json              Inventory records (14 products)
      inbound.json                Stock-in records (30 entries)
      outbound.json               Stock-out records (336 entries)

## How to Run

### Prerequisites

- Java 8 or higher installed

### Compilation

    javac -d bin src/zyhinventory/*.java

### Execution

    java -cp bin zyhinventory.ZYHInventoryApp

**Note:** The application runs in local-only mode. Data is read from the `data/` directory and does not require network access.

## Data Summary

| Dataset | Records |
| --- | --- |
| Inventory | 14 |
| Stock-in | 30 |
| Stock-out | 336 |

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

