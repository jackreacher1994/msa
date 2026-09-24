package com.msa.customer.core.domain.queries;

import com.msa.customer.core.domain.constants.CustomerConstants;

/** Query: list customers, capped to protect the service from unbounded reads. */
public record FindAllCustomersQuery(int limit) {

    public FindAllCustomersQuery {
        if (limit <= 0 || limit > CustomerConstants.MAX_QUERY_LIMIT) {
            limit = CustomerConstants.MAX_QUERY_LIMIT;
        }
    }
}
