package it.sara.demo.web.user.request;

import it.sara.demo.service.user.criteria.CriteriaGetUsers.OrderType;
import it.sara.demo.web.request.GenericRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUsersRequest extends GenericRequest {

    private String query;
    private int offset;
    private int limit;
    private OrderType order;
}


