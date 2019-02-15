package duong.cache.hd.base.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

    public static final char DEFAULT_REPLACE_CHAR = ' ';
    public static final String DEFAULT_REPLACE = String.valueOf(" ");
    public static final Pattern DIACRITICS_AND_FRIENDS = Pattern
            .compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
    private static final List<? extends DateTimeFormatter> DATE_FORMATS = Arrays.asList(
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"), DateTimeFormat.forPattern("yyyy-MM-dd  HH:mm:ss"),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"), DateTimeFormat.forPattern("yyyy-MM-dd"),
            DateTimeFormat.forPattern("yyyyMMdd"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormat.forPattern("dd-MM-yyy"), DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    private static final ImmutableMap<String, String> NONDIACRITICS = ImmutableMap.<String, String>builder()

            // Remove crap strings with no sematics
            .put(".", DEFAULT_REPLACE).put("\"", DEFAULT_REPLACE).put("'", DEFAULT_REPLACE)

            // Keep relevant characters as seperation
            .put("$", DEFAULT_REPLACE).put("%", DEFAULT_REPLACE).put("]", DEFAULT_REPLACE).put("[", DEFAULT_REPLACE)
            .put(")", DEFAULT_REPLACE).put("(", DEFAULT_REPLACE).put("=", DEFAULT_REPLACE).put("!", DEFAULT_REPLACE)
            .put("/", DEFAULT_REPLACE).put("\\", DEFAULT_REPLACE).put("&", DEFAULT_REPLACE).put(",", DEFAULT_REPLACE)
            .put("?", DEFAULT_REPLACE).put("°", DEFAULT_REPLACE)
            // Remove ?? is diacritic?
            .put("|", DEFAULT_REPLACE).put("<", DEFAULT_REPLACE).put(">", DEFAULT_REPLACE).put(";", DEFAULT_REPLACE)
            .put(":", DEFAULT_REPLACE).put("_", DEFAULT_REPLACE).put("#", DEFAULT_REPLACE).put("~", DEFAULT_REPLACE)
            .put("+", DEFAULT_REPLACE).put("*", DEFAULT_REPLACE)

            // Replace non-diacritics as their equivalent characters
            .put("\u0141", "l")
            // BiaLystock
            .put("\u0142", "l")
            // Bialystock
            .put("ß", "ss").put("æ", "ae").put("ø", "o").put("©", "c").put("\u00D0", "d")
            // All Ð ð from http://de.wikipedia.org/wiki/%C3%90
            .put("\u00F0", "d").put("\u0110", "d").put("\u0111", "d").put("\u0189", "d").put("\u0256", "d")
            .put("\u00DE", "th") // thorn
            // Þ
            .put("\u00FE", "th") // thorn þ
            .build();

    public static Date getUTC() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return Date.from(now.toInstant());
    }

    public static Date getNow() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        String formatedText = format.format(getUTC());
        Date result = Util.convertToDate(formatedText);

        return result;
    }

    public static Date addDate(Date source, int days) {
        val c = Calendar.getInstance();
        c.setTime(source);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static Date addMinutes(Date source, int minutes) {
        val c = Calendar.getInstance();
        c.setTime(source);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    public static Date convertToLocal(Date date) {
        if (date == null)
            return null;
        ZonedDateTime zoneDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime localZoneDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime zoneDateTimeSameLocal = localZoneDateTime.withZoneSameLocal(ZoneId.of("UTC"));
        return Date.from(zoneDateTimeSameLocal.toInstant());
    }

    public static String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String getCountResponse(int count) {
        return String.format("count:%d", count);
    }

    public static String getIntResponse(Integer result) {
        return String.format("\"result\":%d", result);
    }

    public static String getSeoAlias(String input, int maxLength) {
        input = input.replaceAll("[^A-Za-z0-9]", "-");
        input = input.replaceAll("\\-+", "-");
        if (maxLength > 80 || maxLength <= 0) {
            maxLength = 80;
        }
        if (maxLength > 0 && input.length() > maxLength) {
            input = input.substring(0, maxLength);
        }
        return input.toLowerCase();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") == null;
    }

    public static boolean checkStringContainsOnlySpecialCharactersForAlias(String input) {
        if (input == null)
            return true;
        String splChrs = "/@#$^&_+=()%!.?*";
        return input.matches("[" + splChrs + "]+");
    }

    public static String getSeoAlias(String orig) {
        orig = orig.replaceAll("-", " ");
        String str = orig;
        if (str == null) {
            return null;
        }
        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        str = mtch.replaceAll(" ");

        str = removeVietnameseChar(str);
        str = stripDiacritics(str);
        str = stripNonDiacritics(str);
        str = str.trim();
        str = str.replaceAll("\\s+", "-");

        if (str.length() == 0) {
            // Ugly special case to work around non-existing empty strings
            // in Oracle. Store original crapstring as simplified.
            // It would return an empty string if Oracle could store it.
            str = orig;
        }
        if (str.length() > 140) {
            str = str.substring(0, 140);
        }
        return str.toLowerCase();
    }

    public static String removeInvalidChar(String orig) {
        orig = orig.replaceAll("-", "");
        String str = orig;
        if (str == null) {
            return null;
        }
        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        str = mtch.replaceAll(" ");

        str = removeVietnameseChar(str);
        str = stripDiacritics(str);
        str = stripNonDiacritics(str);
        str = str.replaceAll("\\s+", "");

        if (str.length() == 0) {
            // Ugly special case to work around non-existing empty strings
            // in Oracle. Store original crapstring as simplified.
            // It would return an empty string if Oracle could store it.
            str = orig;
        }
        if (str.length() > 140) {
            str = str.substring(0, 140);
        }
        return str.toLowerCase();
    }

    private static String stripNonDiacritics(String orig) {
        StringBuffer ret = new StringBuffer();
        String lastchar = null;
        for (int i = 0; i < orig.length(); i++) {
            String source = orig.substring(i, i + 1);
            String replace = NONDIACRITICS.get(source);
            String toReplace = replace == null ? String.valueOf(source) : replace;
            if (DEFAULT_REPLACE.equals(lastchar) && DEFAULT_REPLACE.equals(toReplace)) {
                toReplace = "";
            } else {
                lastchar = toReplace;
            }
            ret.append(toReplace);
        }
        if (ret.length() > 0 && DEFAULT_REPLACE_CHAR == ret.charAt(ret.length() - 1)) {
            ret.deleteCharAt(ret.length() - 1);
        }
        return ret.toString();
    }

    public static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        return str;
    }

    public static String removeVietnameseChar(String input) {
        input = input.replaceAll("[à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ]", "a");
        input = input.replaceAll("[è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ]", "e");
        input = input.replaceAll("[ì|í|ị|ỉ|ĩ]", "i");
        input = input.replaceAll("[ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ]", "o");
        input = input.replaceAll("[ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ]", "u");
        input = input.replaceAll("[ỳ|ý|ỵ|ỷ|ỹ]", "y");
        input = input.replaceAll("[đ]", "d");
        input = input.replaceAll("[À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ]", "A");
        input = input.replaceAll("[È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ]", "E");
        input = input.replaceAll("[Ì|Í|Ị|Ỉ|Ĩ]", "I");
        input = input.replaceAll("[Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ]", "O");
        input = input.replaceAll("[Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ]", "U");
        input = input.replaceAll("[Ỳ|Ý|Ỵ|Ỷ|Ỹ]", "Y");
        input = input.replaceAll("[Đ]", "D");

        return input;
    }

    public static boolean isImage(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        switch (extension.toLowerCase()) {
            case "gif":
            case "jpg":
            case "jpeg":
            case "png":
            case "ico":
                return true;
            default:
                return false;
        }
    }

    public static boolean isFlash(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return extension.equalsIgnoreCase("swf");
    }

    public static byte[] base64ToByte(String base64Str) {
        return Base64.decodeBase64(base64Str);
    }

    public static String byteToBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    public static <T> T getFirstOrDefault(List<T> list) {
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static <T extends BaseEntity> T getFirstOrDefaultById(List<T> list, int id) {
        if (!list.isEmpty()) {
            return getFirstOrDefault(
                    list.stream().filter(a -> Integer.compare(a.getId(), id) == 0).collect(Collectors.toList()));
        } else {
            return null;
        }
    }

    public static <T> boolean isNullOrEmpty(List<T> list) {
        if (list == null || list.isEmpty())
            return true;

        return false;
    }

    public static <T> String joinList(List<T> list, String delimiter) {
        if (isNullOrEmpty(list))
            return "";
        if (delimiter == null) {
            delimiter = ",";
        }
        return list.stream().map(T::toString).collect(Collectors.joining(delimiter));
    }

    public static <T> String joinList(List<T> list) {
        return joinList(list, ",");
    }

    public static <T> String joinListIsChar(List<T> list) {
        if (isNullOrEmpty(list))
            return "";

        return "'" + list.stream().map(T::toString).collect(Collectors.joining("','")) + "'";

    }

    public static <T> List<T> removeNull(List<T> list) {
        return list.stream().filter(a -> a != null).collect(Collectors.toList());
    }

    public static String buildUri(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (StringUtils.isEmpty(args[i]))
                continue;

            builder.append(args[i]);
            if (i != (args.length - 1))
                builder.append("/");
        }
        return builder.toString().replace("//", "/");
    }

    public static String getContentType(String fileName) {

        if (StringUtils.isBlank(fileName))
            return StringUtils.EMPTY;

        String extension = FilenameUtils.getExtension(fileName);
        switch (extension.toLowerCase()) {
            case "bwt":
                return "text/x-bwt";
            case "html":
                return "text/html";
            case "css":
            case "scss":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "gif":
                return "image/gif";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "ico":
                return "image/x-icon";
            case "svg":
                return "image/svg+xml";
            case "eot":
                return "application/vnd.ms-fontobject";
            case "ttf":
                return "application/x-font-truetype";
            case "woff":
                return "application/font-woff";
            case "woff2":
                return "application/font-woff2";
            case "zip":
                return "application/zip";
            case "swf":
                return "application/x-shockwave-flash";
            case "xlsx":
                return "application/vnd.ms-excel";
            default:
                return StringUtils.EMPTY;
        }
    }

    public static String getExtension(String contentType) {

        switch (contentType.toLowerCase()) {
            case "text/x-bwt":
                return "bwt";
            case "text/html":
                return "html";
            case "text/css":
                return "css";
            case "application/javascript":
                return "js";
            case "application/json":
                return "json";
            case "image/gif":
                return "gif";
            case "image/jpeg":
            case "image/pjpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/x-icon":
                return "ico";
            case "image/svg+xml":
                return "svg";
            case "application/vnd.ms-fontobject":
                return "eot";
            case "application/x-font-truetype":
                return "ttf";
            case "application/font-woff":
                return "woff";
            case "application/x-shockwave-flash":
                return "swf";
            default:
                return StringUtils.EMPTY;
        }
    }

    public static boolean isPublicRead(String fileName) {
        if (StringUtils.isBlank(fileName))
            return false;

        if (fileName.toLowerCase().endsWith("css.bwt") || fileName.toLowerCase().endsWith("js.bwt"))
            return true;

        String extension = FilenameUtils.getExtension(fileName);
        switch (extension.toLowerCase()) {
            case "css":
            case "scss":
            case "js":
            case "gif":
            case "jpg":
            case "jpeg":
            case "png":
            case "svg":
            case "eot":
            case "ttf":
            case "woff":
            case "swf":
            case "woff2":
            case "ico":
                return true;
            default:
                return false;
        }
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatListUnicodeText(List<String> values) {
        if (Util.isNullOrEmpty(values))
            return "";

        List<String> formatedValues = new ArrayList<String>();
        for (int i = 0; i < values.size(); i++) {
            formatedValues.add(formatUnicodeText(values.get(i)));
        }

        return Util.joinList(formatedValues, ",");
    }

    public static String formatUnicodeText(String value) {
        return "N'" + StringUtils.replace(value, "'", "''") + "'";
    }

    public static boolean isPublished(Date publishDate) {
        if (publishDate != null && publishDate.before(Util.getUTC()))
            return true;

        return false;
    }

    public static List<String> getListTagsFromString(String tags) {
        if (tags != null) {
            tags = tags.trim();
            if (!StringUtils.equals(tags, StringUtils.EMPTY)) {
                val arr = Arrays.asList(tags.split(",")).stream().filter(a -> !StringUtils.isBlank(a))
                        .map(a -> a.trim()).distinct().collect(Collectors.toList());
                if (!arr.isEmpty()) {
                    return arr;
                }
            }
        }
        return null;
    }

    public static int containsIgnoreCase(List<String> list, String soughtFor) {
        int find = -1;
        for (String current : list) {
            find++;
            if (current.equalsIgnoreCase(soughtFor)) {
                return find;
            }
        }
        return -1;
    }

    public static String getFullname(String firstName, String lastName) {
        if (StringUtils.isBlank(firstName)) {
            firstName = StringUtils.EMPTY;
        }
        if (StringUtils.isBlank(lastName)) {
            lastName = StringUtils.EMPTY;
        }
        return (lastName + " " + firstName).trim();
    }

    public static List<Integer> getListIntFromSplitString(String input) {
        List<Integer> numbers = new ArrayList<Integer>();
        if (!StringUtils.isBlank(input)) {
            for (String number : input.split(",")) {
                if (StringUtils.isNumeric(number)) {
                    numbers.add(Integer.parseInt(number));
                }
            }
        }
        return numbers;
    }

    public static String hmac(String key, String data) {
        try {
            val sha256_HMAC = Mac.getInstance("HmacSHA256");
            val secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return new String(Base64.encodeBase64(sha256_HMAC.doFinal(data.getBytes("UTF-8")))).trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static String substring(String input, int length) {

        if (StringUtils.isBlank(input))
            return input;

        if (input.length() > length)
            input = input.substring(0, length - 1);

        return input;
    }

    public static String formatDate(Date date, String format) {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        // Get the date today using Calendar object.
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        return df.format(date);
    }

    @SuppressWarnings("deprecation")
    public static Date processDate(Date date) {
        if (date != null && date.before(new Date(70, 11, 1))) {
            date = Util.getUTC();
        }
        return date;
    }

    /**
     * This method compares 2 BigDecimal objects for equality. It takes care of
     * null object and that was the necessity of having it. To use this function
     * most efficiently pass the possibly null object before the not null
     * object.
     *
     * @param pNumber1
     * @param pNumber2
     * @return boolean
     */
    public static boolean isEqual(BigDecimal pNumber1, BigDecimal pNumber2) {
        if (pNumber1 == null) {
            if (pNumber2 == null)
                return true;
            return false;
        }
        if (pNumber2 == null)
            return false;
        return pNumber1.compareTo(pNumber2) == 0;
    }

    public static boolean existFieldInJsonNode(JsonNode node, String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        boolean found = false;
        if (key.contains(".")) {
            val lstField = key.split(".");
            JsonNode tempNode = node.path(lstField[0]);
            found = !tempNode.isMissingNode();
            if (found && lstField.length > 1) {
                int index = 1;
                while (found && index < lstField.length) {
                    tempNode = tempNode.path(lstField[index]);
                    found = !tempNode.isMissingNode();
                    index++;
                }
            }
        } else {
            found = !node.path(key).isMissingNode();
        }
        return found;
    }

    public static <T> void validatteObject(T entity) {
        Map<String, Object> errors = new HashMap<String, Object>();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = new HashSet<ConstraintViolation<T>>();
        constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            for (val constraintViolation : constraintViolations) {
                errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            }
            throw new FormValidateException(errors);
        }
    }

    public static <T> HashMap<String, Object> validateObject(T entity) {
        HashMap<String, Object> errors = new HashMap<String, Object>();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = new HashSet<ConstraintViolation<T>>();
        constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            for (val constraintViolation : constraintViolations) {
                errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            }
        }
        return errors;
    }

    public static Date convertToDate(String source) {
        if (!StringUtils.isBlank(source)) {
            for (val fmt : DATE_FORMATS) {
                try {
                    val d = fmt.parseLocalDateTime(source);

                    return d.toDate();
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
        }
        return null;
    }

    public static Date addDays(Date date, int days) {
        if (date == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        return cal.getTime();
    }

    public static Date convertToDateWithoutTime(String source) {
        if (!StringUtils.isBlank(source)) {
            val fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            try {
                val d = fmt.parseLocalDateTime(source);
                return d.toDate();
            } catch (IllegalArgumentException e) {
            }
        }
        return null;
    }

    public static String getCount(int totalRecord, int limit) {
        double traffic = (double) totalRecord / (double) limit;
        int totalPage = (int) Math.ceil(traffic);
        return "{\"total_record\":" + totalRecord + ",\"total_page\":" + totalPage + "}";
    }

    public static int[] joinArrays(int[] array1, int[] array2) {
        int[] result = ArrayUtils.addAll(array1, array2);
        return result;
    }

    public static <T> boolean isBlank(List<T> list) {
        if (list == null || list.size() == 0)
            return true;
        else
            return false;
    }

    public static <T> boolean isBlank(T[] array) {
        if (array == null || array.length == 0)
            return true;
        else
            return false;
    }

    public static String joinStringArray(String[] array) {
        List<String> listString = Arrays.asList(array);
        return joinList(listString, ",");
    }

    public static String joinStringArray(int[] array) {
        List<int[]> listInt = Arrays.asList(array);
        return joinList(listInt, ",");
    }

    public static String joinIntArray(int[] tokens, String delimiter) {
        if (tokens == null)
            return "";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            if (i > 0 && delimiter != null) {
                result.append(delimiter);
            }
            result.append(String.valueOf(tokens[i]));
        }
        return result.toString();
    }

    public static String genDirector(int tenantId) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int date = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String directoryString = "C:\\" + date + "-" + month + "-" + year + "\\" + tenantId;
        File directory = new File(directoryString);
        if (!directory.exists())
            directory.mkdirs();
        return directoryString;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> clazz) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields = getAllFields(fields, clazz.getSuperclass());
        }
        return fields;
    }

    public static <T> T convertObject(T obj1, T obj2) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = obj1.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Object fieldValue1 = field.get(obj1);
            Object fieldValue2 = field.get(obj2);
            if (fieldValue1 != null && !fieldValue1.equals(fieldValue2))
                fieldValue2 = fieldValue1;
            field.set(obj2, fieldValue2);
        }
        return obj2;
    }

    public static <T> T updatePartialObjectFromJson(T obj1, T obj2, JsonNode node)
            throws IllegalArgumentException, IllegalAccessException {
        if (obj1 == null)
            return null;
        if (obj2 == null)
            return obj1;
        Class<?> clazz = obj1.getClass();
        List<Field> fields = new ArrayList<Field>();
        fields = getAllFields(fields, clazz);

        List<String> attributeNames = IteratorUtils.toList(node.fieldNames());
        for (String attributeName : attributeNames) {
            String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, attributeName);
            Optional<Field> optionalField = fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();
            if (!optionalField.isPresent())
                continue;
            Field field = optionalField.get();
            field.setAccessible(true);

            field.set(obj2, field.get(obj1));

        }

        return obj2;
    }

    public static boolean setObjectAttribute(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                return false;
            } catch (Exception e) {
                e.getMessage();
                return false;
            }
        }
        return false;
    }

    public static int[] convertStringToIntArray(String input) {
        HashMap<String, Object> errors = new HashMap<String, Object>();

        if (StringUtils.isBlank(input))
            return null;
        ;
        input = input.trim();
        List<String> listString = Arrays.asList(input.split(","));
        if (Util.isBlank(listString))
            return null;
        else {
            int[] result = new int[listString.size()];
            for (int i = 0; i < listString.size(); i++) {
                String item = listString.get(i).trim();
                try {
                    int intValue = Integer.parseInt(item);
                    result[i] = intValue;
                } catch (Exception e) {
                    errors.put(item, "Sai định dạng số");
                }
            }
            ;
            if (errors.size() > 0) {
                throw new FormValidateException(errors);
            }
            return result;
        }
    }

    public static int convertStringToInt(String input) {
        HashMap<String, Object> errors = new HashMap<String, Object>();
        try {
            if (StringUtils.isBlank(input))
                return 0;
            input = input.trim();
            int result = Integer.parseInt(input);
            return result;
        } catch (NumberFormatException e) {
            errors.put(input, "Không đúng định dạng số");
            throw new FormValidateException(errors);
        }
    }

    public static Boolean convertStringToBoolean(String input) {
        HashMap<String, Object> errors = new HashMap<String, Object>();
        if (StringUtils.isBlank(input))
            return null;
        input = input.trim();
        if (input.equals("true") || input.equals("True") || input.equals("TRUE"))
            return true;
        if (input.equals("false") || input.equals("False") || input.equals("FALSE"))
            return false;
        errors.put(input, "Không đúng định dạng dữ liệu");
        throw new FormValidateException(errors);
    }

    public static String convertCamelToSnakeCase(String input) {
        StringBuilder builder = new StringBuilder(input);
        for (int i = 0; i < builder.toString().length(); i++) {
            if (Character.isUpperCase(builder.toString().charAt(i))) {
                char lowerCase = Character.toLowerCase(builder.toString().charAt(i));
                builder.replace(i, i + 1, "_" + lowerCase);
                i++;
            }
        }
        return builder.toString();
    }

    public static <T> boolean equal(T obj1, T obj2) {
        boolean result = true;
        if (obj1 == null && obj2 != null)
            result = false;
        if (obj1 != null && obj2 == null)
            result = false;
        if (obj1 == null && obj2 == null)
            result = true;
        if (obj1 != null && obj2 != null) {
            result = obj1.equals(obj2);
        }
        return result;
    }

    public static int genLocalDateKey(Date date) {
        if (date == null)
            return 0;
        ZonedDateTime localZoneDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("Asia/Ho_Chi_Minh"));
        String sYear = String.valueOf(localZoneDateTime.getYear());
        String sMonth = String.valueOf(localZoneDateTime.getMonthValue());
        if (sMonth.length() == 1) {
            sMonth = "0" + sMonth;
        }
        String sDate = String.valueOf(localZoneDateTime.getDayOfMonth());
        if (sDate.length() == 1) {
            sDate = "0" + sDate;
        }
        return Integer.parseInt(sYear + sMonth + sDate);
    }

    public static String generateFileName(String initName, int tenantId, String extension) {
        Date nowUTC = Util.getUTC();
        Date now = DateUtils.addHours(nowUTC, 7);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int milisecond = cal.get(Calendar.MILLISECOND);
        String dateStr = year + "-" + month + "-" + day + "_" + hour + "-" + minute + "-" + second + "-" + milisecond;

        String md5 = md5(tenantId + "_" + dateStr);

        String fileName = initName + "-" + md5 + "." + extension;

        return fileName;
    }

    public static <T> List<T> getNonDuplicateList(List<T> originalList) {
        Set<T> set = new HashSet<T>(originalList);
        return new ArrayList<T>(set);
    }

    public static <T> boolean compareSet(Set<T> set1, Set<T> set2) {
        if (!set1.containsAll(set2))
            return false;
        if (!set2.containsAll(set1))
            return false;
        return true;
    }

    public static int calculateStringDistance(String str1, String str2){

        return 0;
    }

    public static boolean isPrivateIP(String ip) {
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException exception) {
            return false;
        }
        return address.isSiteLocalAddress() || address.isAnyLocalAddress() || address.isLinkLocalAddress()
                || address.isLoopbackAddress() || address.isMulticastAddress();
    }
    public static boolean isPrivateIP(HttpServletRequest request) {
        return isPrivateIP(getRemoteIP(request));
    }

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
            // get first ip from proxy ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static <T> List<T> mergeList(List<T> list1, List<T> list2){
        List<T> result = new ArrayList<T>(list1);
        result.addAll(list2);
        return result;
    }

    public static int getLimit(HashMap<String, String> queryMaps){
        if(queryMaps.get("limit") == null)
            return 20;
        Integer limit = Integer.valueOf(queryMaps.get("limit"));
        if(limit < 1 || limit > 250)
            return 20;
        return limit;
    }
    public static int getPage(HashMap<String, String> queryMaps){
        if(queryMaps.get("page") == null)
            return 1;
        Integer page = Integer.valueOf(queryMaps.get("page"));
        if(page < 1)
            return 1;
        return page;
    }

    public static HttpServletRequest getCurrentHttpRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        }
        return null;
    }
}
