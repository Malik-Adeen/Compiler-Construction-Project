import java.util.*;

/**
 * Symbol Table for CL Language Compiler
 * Stores information about variables declared in the program
 */
public class SymbolTable {

    /**
     * Inner class to represent a symbol entry
     */
    private class SymbolEntry {
        String name;
        String type;
        String value;
        int lineNumber;

        public SymbolEntry(String name, String type, String value, int lineNumber) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return String.format("%-15s %-10s %-20s Line: %d",
                    name, type, value, lineNumber);
        }
    }

    // HashMap to store symbols
    private HashMap<String, SymbolEntry> table;

    /**
     * Constructor
     */
    public SymbolTable() {
        table = new HashMap<>();
    }

    /**
     * Add a symbol to the table
     */
    public void addSymbol(String name, String type, String value, int lineNumber) {
        SymbolEntry entry = new SymbolEntry(name, type, value, lineNumber);
        table.put(name, entry);
    }

    /**
     * Check if a symbol exists
     */
    public boolean exists(String name) {
        return table.containsKey(name);
    }

    /**
     * Get the type of a variable
     */
    public String getType(String name) {
        SymbolEntry entry = table.get(name);
        return (entry != null) ? entry.type : null;
    }

    /**
     * Get the value of a variable
     */
    public String getValue(String name) {
        SymbolEntry entry = table.get(name);
        return (entry != null) ? entry.value : null;
    }

    /**
     * Get line number where variable was declared
     */
    public int getLineNumber(String name) {
        SymbolEntry entry = table.get(name);
        return (entry != null) ? entry.lineNumber : -1;
    }

    /**
     * Print the symbol table
     */
    public void print() {
        System.out.println("=".repeat(70));
        System.out.println("SYMBOL TABLE");
        System.out.println("=".repeat(70));
        System.out.printf("%-15s %-10s %-20s %s%n", "Name", "Type", "Value", "Line");
        System.out.println("-".repeat(70));

        if (table.isEmpty()) {
            System.out.println("(No variables declared)");
        } else {
            // Sort by line number for better readability
            List<SymbolEntry> entries = new ArrayList<>(table.values());
            entries.sort((a, b) -> Integer.compare(a.lineNumber, b.lineNumber));

            for (SymbolEntry entry : entries) {
                System.out.println(entry);
            }
        }
        System.out.println("=".repeat(70));
        System.out.println("Total variables: " + table.size());
    }

    /**
     * Get table size
     */
    public int size() {
        return table.size();
    }

    /**
     * Clear the table
     */
    public void clear() {
        table.clear();
    }
}