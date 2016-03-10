package com.tatiana.inventory.controller;

import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.exception.NonDeletableObjectException;
import com.tatiana.inventory.service.ServiceService;
import com.tatiana.inventory.service.SubscriptionService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/service")
public class ServiceController {
    @Autowired
    ServiceService serviceService;
    @Autowired
    SubscriptionService subscriptionService;

    /**
     * Finds service with requested id
     * @param id
     * @return HttpEntity<Service> - with HttpStatus.OK if it is all OK
     * @throws ObjectNotFoundException
     */
    @RequestMapping(value="/{id}", method= RequestMethod.GET)
    public HttpEntity<Service> getItem(@PathVariable("id") Integer id) throws ObjectNotFoundException {
        Service service = serviceService.find(id);
        return new ResponseEntity( service, HttpStatus.OK );
    }

    /**
     * Updates service with requested id
     * @param service
     * @return HttpEntity<Service> - with HttpStatus.OK if it is all OK
     * @throws ObjectNotFoundException
     */
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
    public HttpEntity<Service> update(@PathVariable("id") Integer id, @RequestBody Service service)
            throws ObjectNotFoundException{
        Service savedService = serviceService.update(service, service.getId());
        return new ResponseEntity( savedService, HttpStatus.OK );
    }

    /**
     * Creates new service
     * @param service
     * @return HttpEntity<Service>
     */
    @RequestMapping(method=RequestMethod.PUT)
    public HttpEntity<Service> create(@RequestBody Service service){
        Service savedService = serviceService.create(service);
        return new ResponseEntity( savedService, HttpStatus.OK );
    }

    /**
     * Finds all services
     * @return HttpEntity<List<Service>>
     */
    @RequestMapping(method=RequestMethod.GET)
    public HttpEntity<List<Service>> getAll(){
        List<Service> services= serviceService.findAll();
        if( services.size() == 0 ){
            new ResponseEntity( HttpStatus.NO_CONTENT );
        }
        return new ResponseEntity( services, HttpStatus.OK );
    }

    /**
     * Deletes service with requested id if it is no subscription on this service
     * @param id
     * @return HttpEntity<String>
     * @throws ObjectNotFoundException
     * @throws NonDeletableObjectException
     */
    @RequestMapping(value = "/{id}",method=RequestMethod.DELETE)
    public HttpEntity<String> deleteItem(@PathVariable("id") Integer id) throws ObjectNotFoundException, NonDeletableObjectException {
        Boolean subscribed = subscriptionService.existsSubscriptionWithServiceId(id);
        serviceService.deleteNonSubscribedById( id, subscribed );
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
