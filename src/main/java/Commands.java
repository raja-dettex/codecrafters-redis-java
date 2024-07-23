import java.util.List;

public class Commands {
    private String strValue;
    private int intValue;
    private List<Commands> listValue; // Use List<Commands> to handle nested structures

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public List<Commands> getListValue() {
        return listValue;
    }

    public void setListValue(List<Commands> listValue) {
        this.listValue = listValue;
    }

    @Override
    public String toString() {
        if (strValue != null) return "String: " + strValue;
        if (intValue != 0) return "Integer: " + intValue;
        if (listValue != null) return "List: " + listValue.toString();
        return "Empty";
    }
}
