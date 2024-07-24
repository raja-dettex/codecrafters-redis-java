import java.util.List;

public class Dtype {

    public char type;
    private String strValue;
    private int intValue;

    private List<Dtype> listValue; // Use List<Commands> to handle nested structures

    public void setType(char type) {
        this.type = type;
    }
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

    public List<Dtype> getListValue() {
        return listValue;
    }

    public void setListValue(List<Dtype> listValue) {
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
