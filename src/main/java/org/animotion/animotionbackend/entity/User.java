package org.animotion.animotionbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true) // Email must be unique
    private String email;

    private String name;

    private String image; // avatar URL

    @JsonIgnore
    private String password; // Only for AuthProvider.LOCAL

    private AuthProvider provider; // LOCAL, GOOGLE, GITHUB...

    private String providerId; // auth provider unique id
}
