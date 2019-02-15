package duong.cache.hd.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Getter
@Setter
public class DomainValidationException extends FormValidateException {

    private static final long serialVersionUID = 1L;

    public DomainValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }

    public DomainValidationException(Map<String, Object> messageResult) {
        super(messageResult);
    }

    public DomainValidationException(BindingResult bindingResult,
                                     Map<String, Object> messageResult) {
        super(bindingResult, messageResult);
    }

    public void add(String key, Object value) {
        this.messageResult.put(key, value);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (this.messageResult != null && this.messageResult.size() > 0) {
            for (val entry : this.messageResult.entrySet()) {
                buf.append(entry.getKey() + ": " + entry.getValue() + "/n");
            }
        }
        return buf.toString();
    }
}
