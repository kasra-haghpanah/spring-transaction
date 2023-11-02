package com.example.propagation.ddd.service;

import com.example.propagation.ddd.dto.SupportDTO;
import com.example.propagation.ddd.model.Customer;
import com.example.propagation.ddd.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupportService {
   /*
    https://www.baeldung.com/spring-transactional-propagation-isolation
    Propagation:
    ----------------------------------------------------------------------
    REQUIRED:      if(tr) => used else new-tr
    SUPPORTS:      if(tr) => used else non-tr
    MANDATORY:     if(tr) => use else throw exception
    REQUIRES_NEW:  if(tr) => suspend the new-tr
    NOT_SUPPORTED: if(tr) => suspend then non-tr
    NEVER:         if(tr) => exception
    NESTED:        if(throw) => rollback to last save point else REQUIRED
    ----------------------------------------------------------------------
    Isolation:
    ----------------------------------------------------------------------
    Dirty read: read the uncommitted change of a concurrent transaction
    Nonrepeatable read: get different value on re-read of a row if a concurrent transaction updates the same row and commits
    Phantom read: get different rows after re-execution of a range query if another transaction adds or removes some rows in the range and commits
    ----------------------------------------------------------------------
    DEFAULT:
    READ_UNCOMMITTED:
    READ_COMMITTED:
    REPEATABLE_READ:
    SERIALIZABLE:
    ----------------------------------------------------------------------

*/
    private final UserService userService;
    private final CustomerService customerService;

    public SupportService(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    @Transactional(value = "propagationTM", propagation = Propagation.NESTED)
    public SupportDTO save(SupportDTO supportDTO) {
        SupportDTO dto = new SupportDTO();
        User user = userService.save(supportDTO.getUser());
        dto.setUser(user);
        try {
            Customer customer = customerService.saveCustomer(supportDTO.getCustomer());
            dto.setCustomer(customer);
        } catch (Exception e) {

        }


        return dto;
    }

}
