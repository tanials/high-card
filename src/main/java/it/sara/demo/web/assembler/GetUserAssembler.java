package it.sara.demo.web.assembler;

import it.sara.demo.service.user.criteria.CriteriaGetUsers;
import it.sara.demo.service.user.result.GetUsersResult;
import it.sara.demo.web.user.request.GetUsersRequest;
import it.sara.demo.web.user.response.GetUsersResponse;
import org.springframework.stereotype.Component;

@Component
public class GetUserAssembler {

    public CriteriaGetUsers toCriteria(GetUsersRequest getUserRequest) {
        CriteriaGetUsers returnValue = new CriteriaGetUsers();
        returnValue.setLimit(getUserRequest.getLimit());
        returnValue.setOffset(getUserRequest.getOffset());
        returnValue.setQuery(getUserRequest.getQuery());
        returnValue.setOrder(getUserRequest.getOrder());

        return returnValue;
    }

    public GetUsersResponse toResponse(GetUsersResult result) {
        GetUsersResponse returnValue = new GetUsersResponse();
        returnValue.setUsers(result.getUsers());
        returnValue.setTotal(result.getTotal());
        return returnValue;
    }
}
