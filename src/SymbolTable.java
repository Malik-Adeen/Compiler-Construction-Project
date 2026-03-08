import java.util.*;

public class SymbolTable {

    private HashMap<String, String> symTab;

    public SymbolTable() {
        symTab = new HashMap<>();
    }

    public void put(String key, String typeAndName) {
        symTab.put(key, typeAndName);
    }

    public String get(String key) {
        return symTab.get(key);
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
        System.out.println("=".repeat(50));
        System.out.println("SYMBOL TABLE");
        System.out.println("=".repeat(50));
        for (Map.Entry<String, String> entry : symTab.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("=".repeat(50));
    }

    public int size() {
        return symTab.size();
    }
}