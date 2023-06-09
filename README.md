# Mielo Database
Mielo database module is a good easy to use wrapper over Java Persistent APIs (JPA). It provides APIs to perform CRUD 
operations on different Hibernate entities.

Here is one example. Here we can get a ```User``` entity using ``` CRUDRepo ```  
```java
package com.mielo.authservice.repositories;

import com.mielo.authservice.entities.User;
import org.mielo.database.CRUDRepo;
import org.mielo.database.Property;
import java.util.Optional;

public class UsersRepository {
    
    CRUDRepo crud;

    public UsersRepository(CRUDRepo crud) {
        this.crud = crud;
    }
    
    public Optional<User> findUserByEmail(String email) {
        return this.crud.findByProperty(User.class, new Property("email", email));
    }
}
```

We can easily integrate this library with ```SpringBoot JPA``` applications by exposing ``` CRUDRepo ``` as ``` @Bean ```
and then use it anywhere we need data.

We can execute native SQL queries with ```CRUDRepo```. We can provide ```RowMapper``` to map the result of the SQL into
our own custom java objects. 


