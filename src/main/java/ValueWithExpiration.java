import java.util.Optional;

public class ValueWithExpiration {
    private String value;
    private Long setTimeStamp;
    private Optional<Long> ttl;
    public ValueWithExpiration(String value, Optional<Long> ttlOptional) {
        this.value = value;
        this.setTimeStamp = System.currentTimeMillis();
        this.ttl = ttlOptional;
    }
    public String getValue() {
        return value;
    }
    public boolean isTtl() {
        return ttl.isPresent();
    }
    public Long getTtl() {
        return ttl.get();
    }

    public boolean isExpired() {
        Long timeElapsed = Long.valueOf(System.currentTimeMillis()) - setTimeStamp;

        if(this.isTtl()) {
            Long ttl = this.getTtl();
            return (timeElapsed > ttl )?true:false;
        }
        return false;
    }

}
