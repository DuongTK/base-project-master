package duong.cache.hd.base.annotation;

import duong.cache.hd.base.common.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class StringInListValidator implements
        ConstraintValidator<StringInList, String> {
    private StringInList _annatation;

    @Override
    public void initialize(StringInList stringInList) {
        this._annatation = stringInList;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = false;
        if (_annatation.allowBlank()) {
            if (StringUtils.isBlank(value)) {
                isValid = true;
            }
        }

        if (!isValid) {
            isValid = ArrayUtils.contains(_annatation.array(), value);
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            String message = String.format("is not in [%s]",
                    Util.joinList(Arrays.asList(_annatation.array())));

            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }
        return isValid;
    }

}
