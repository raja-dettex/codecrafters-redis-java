import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryMap {
    private ConcurrentHashMap<String, String> dict = new ConcurrentHashMap<>();

    private String setToString(Dtype value) {
        switch (value.type) {
            case ':' -> {
                return String.valueOf(value.getIntValue());
            }
            case '$' -> {
                return value.getStrValue();
            }
            default -> {
                break;
            }
        }
        return null;
    }


    public int set(Dtype key, Dtype value) {
        var keystr = this.setToString(key);
        var valuestr = this.setToString(value);
        if(keystr != null && valuestr != null) {
            this.dict.put(keystr, valuestr);
            return 1;
        }
        return -1;
    }
    public String get(Dtype key) {
        var keystr = this.setToString(key);
        return this.dict.get(keystr);
    }

}
