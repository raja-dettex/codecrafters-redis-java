import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryMap {
    private ConcurrentHashMap<String, ValueWithExpiration> dict = new ConcurrentHashMap<>();

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

    public void EvictKeys() throws InterruptedException {
        while(true) {
            this.dict.forEach((key,  valueWithExpiration) -> {
                if(valueWithExpiration.isExpired()) {
                    ValueWithExpiration remove = this.dict.remove(key);
                }
            });
            Thread.sleep(100);
        }
    }




    public int set(Dtype key, Dtype value, Long ttl) {
        ValueWithExpiration valueWithExpiration;
        var keystr = this.setToString(key);
        var valuestr = this.setToString(value);
        if(keystr != null && valuestr != null) {
            Optional<Long> ttlValue;
            if(ttl != -1 ){
                ttlValue = Optional.of(ttl);
                valueWithExpiration = new ValueWithExpiration(valuestr, ttlValue);
                this.dict.put(keystr, valueWithExpiration);
                return 1;
            }
            ttlValue = Optional.empty();
            valueWithExpiration = new ValueWithExpiration(valuestr, ttlValue);
            this.dict.put(keystr, valueWithExpiration);
            return 1;
        }
        return -1;
    }
    public String get(Dtype key) {
        var keystr = this.setToString(key);
        ValueWithExpiration valueWithExpiration = this.dict.get(keystr);
        return (valueWithExpiration != null)?valueWithExpiration.getValue():null;
    }

}
