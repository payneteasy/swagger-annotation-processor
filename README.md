# swagger-annotation-processor

Library for swagger REST services compile time generation.

## Usage

1. Add maven dependency:
    ```xml
    <dependency>
      <groupId>com.payneteasy.swagger.apt</groupId>
      <artifactId>apt</artifactId>
      <version>1.0.0.1</version>
    </dependency>
    <dependency>
      <groupId>com.squareup</groupId>
      <artifactId>javapoet</artifactId>
      <version>1.12.1</version>
      <scope>provided</scope>
    </dependency>
    ```
2. Turn on annotation processor:
    ```xml
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.8.1</version>
      <configuration>
        <annotationProcessors>
          <annotationProcessor>com.payneteasy.swagger.apt.ServicesExportSwaggerProcessor</annotationProcessor>
        </annotationProcessors>
      </configuration>
    </plugin>
    ```
3. Mark service interfaces and/or its methods you want to export to Swagger with `@ExportToSwagger` annotation.  
    Note that only services with interfaces are supported currently.  
    
    Method marked:
    ```java
    public interface IService {
        @ExportToSwagger
        int doSomething(String str);
    }
    ```
    Or interface marked:
    ```
    @ExportToSwagger
    public interface IService {
        int doSomething(String str);
    }
    ```
    
    Service implementation:
    ```
    public class ServiceImpl implements IService {
        @Override
        public int doSomething(String str) {
            return 777;
        }
    }
    ```
4. By default the method name becomes the last path part of the REST method, 
to override it specify @ExportToSwagger value on service method:
    ```
    public interface IService {
        @ExportToSwagger("doSomethingInt")
        int doSomething(int anInt);
        
        @ExportToSwagger("doSomethingString")
        int doSomething(String str);
    }
    ```
5. Implement your controller(s) that uses `Swagger302Generator` and `ServiceInvoker`. See demo.

## Build

```shell script
./build.sh
```

## Demo application

Run `com.payneteasy.swagger.apt.demo.JettyStart`.  
Open <http://localhost:8080/demo/swagger-ui/>.  
Also there is a low-level services description page <http://localhost:8080/demo/api/doc>.
