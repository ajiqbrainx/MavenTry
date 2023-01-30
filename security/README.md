# LB_COMM : Security

Common Library which handles the Security for the incoming request.


## Getting Started

These Instructions show how to use the base library class in our project
for 
* Security

````
Prerequisites 
````
1. You should have added the dependency for security in your project's build.gradle file


#### How to use Security Library

If we need to enable security for the spring boot app, we need to use the following annotation

@EnableDijtaSecurityAndAuditing


````
Example 

@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaSecurityAndAuditing
public class MessageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
    }
```` 


