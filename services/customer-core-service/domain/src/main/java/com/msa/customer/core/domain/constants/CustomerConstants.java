package com.msa.customer.core.domain.constants;

import java.util.regex.Pattern;

public final class CustomerConstants {

    public static final int MAX_FULL_NAME_LENGTH = 100;
    public static final int MAX_QUERY_LIMIT = 100;
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");
    public static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    private CustomerConstants() {
    }
}
