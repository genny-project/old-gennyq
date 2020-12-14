package au.com.crowtech.quarkus.nest.beans;

import javax.inject.Singleton;

@Singleton 
public class Echo {

   public String echo(String val) {
      return val;
   }
}