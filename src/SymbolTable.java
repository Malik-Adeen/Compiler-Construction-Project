import java.util.*;

public class SymbolTable {

    public static class Symbol {
        public String lexeme;
        public String type;
        public String value;

        public Symbol(String lexeme, String type, String value) {
            this.lexeme = lexeme;
            this.type = type;
            this.value = value;
        }
    }

    private HashMap<String, Symbol> symTab;

    public SymbolTable() {
        symTab = new HashMap<>();
    }

    public void put(String lexeme, String type, String value) {
        symTab.put(lexeme, new Symbol(lexeme, type, value));
    }

    public Symbol getSymbol(String lexeme) {
        return symTab.get(lexeme);
    }

    public String getType(String lexeme) {
        Symbol sym = symTab.get(lexeme);
        return sym != null ? sym.type : null;
    }

    public String getValue(String lexeme) {
        Symbol sym = symTab.get(lexeme);
        return sym != null ? sym.value : null;
    }

    public void setValue(String lexeme, String value) {
        Symbol sym = symTab.get(lexeme);
        if (sym != null) {
            sym.value = value;
        }
    }

    public void put(String key, String typeAndName) {

        if (typeAndName.startsWith("int")) {
            put(key, "int", "");
        } else if (typeAndName.startsWith("float")) {
            put(key, "float", "");
        } else if (typeAndName.startsWith("string")) {
            put(key, "string", "");
        } else if (typeAndName.startsWith("char")) {
            put(key, "char", "");
        }
    }

    public String get(String key) {
        Symbol sym = symTab.get(key);
        return sym != null ? (sym.type + sym.lexeme) : null;
    }

    public boolean containsKey(String key) {
        return symTab.containsKey(key);
    }

    public boolean checkValue(String tVal) {
        if (symTab.containsValue(tVal)) {
            return false;
        } else {
            return true;
        }
    }

    public void print() {
        System.out.println("=".repeat(60) + "\n");
        System.out.println(String.format("%-20s %-15s %s", "LEXEME", "TYPE", "VALUE"));
        System.out.println("-".repeat(60));
        for (Symbol sym : symTab.values()) {
            System.out.println(String.format("%-20s %-15s %s", sym.lexeme, sym.type, sym.value));
        }
    }

    public int size() {
        return symTab.size();
    }

    public Collection<Symbol> getAllSymbols() {
        return symTab.values();
    }

    public String exportToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SYMBOL TABLE\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("%-20s %-15s %s\n", "LEXEME", "TYPE", "VALUE"));
        sb.append("=".repeat(60)).append("\n");

        for (Symbol sym : symTab.values()) {
            sb.append(String.format("%-20s %-15s %s\n", sym.lexeme, sym.type, sym.value));
        }

        sb.append("=".repeat(60)).append("\n");
        return sb.toString();
    }
}