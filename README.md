# swagger-annotation-processor

Library for Swagger REST services generation by java services marked with special annotations.
REST services are generated both in compile time(annotation processor) and in runtime.

Supports Swagger 3.0.2 (OpenAPI 3.0.2).  
Swagger 3.0.2 json specification: https://swagger.io/specification/.

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
    ```java
    @ExportToSwagger
    public interface IService {
        int doSomething(String str);
    }
    ```
    
    Service implementation:
    ```java
    public class ServiceImpl implements IService {
        @Override
        public int doSomething(String str) {
            return 777;
        }
    }
    ```
4. By default the method name becomes the last path part of the REST method, 
to override it specify `@ExportToSwagger` `value` on service method:
    ```java
    public interface IService {
        @ExportToSwagger("doSomethingInt")
        int doSomething(int anInt);
        
        @ExportToSwagger("doSomethingString")
        int doSomething(String str);
    }
    ```
5. For service survive on service method parameters rename use `@MethodParam` annotation on method parameters:
    ```java
    @ExportToSwagger
    public interface IService {
        void doSomething(@MethodParam("anInt2") int anInt);
        void doSomethingElse(@MethodParam("anInt2") int anInt, @MethodParam("aString2") String aString);
    }
    ```
6. Implement your controller(s) that uses `Swagger302Generator` and `ServiceInvoker`. See demo.

## Build

```shell script
./build.sh
```

## Demo application

Run `com.payneteasy.swagger.apt.demo.JettyStart`.  
Open <http://localhost:8080/demo/swagger-ui/>.  
Also there is a low-level services description page <http://localhost:8080/demo/api/doc>.
