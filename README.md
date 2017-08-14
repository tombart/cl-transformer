**Prototype**

Once started, it listens on two queues for both: messages(queue: feeds) and transforming function(classBodyThatExtendsFunction).
This is temporary solution for proof of concept.

**How To Run**

1. Install docker (https://docs.docker.com/docker-for-mac/install/)
2. cd to cl-transformer project directory
3. run:

        docker-compose up --build
    
    - Once docker started you should see logging printed out (connected to rabbit)
    - Go to http://localhost:15672 (username: guest, password: guest)
    - Go to Queues -> feeds 
    - Send message: 12345 - you should see in logs: 12345
    - Go to Queues -> classBodyThatExtendsFunction and send message:
    
            override def apply(message: String): String = {
                  printMe()
                  message.reverse
                }
                def printMe(): Unit = {
                  println("Executing Function: reverse String")
                }
        
    - Send message: 12345 - you should see in logs: Executing Function: reverse String 54321
    
    
**Shut down**

        ctrl + C
If you still see docker containers running:
           
           docker-compose down
    
**Structure of a function**

Transformer is trying to compile to a class that extends Function[String, String]. Any body is acceptable as long as 'apply' method is overriden. Example:

    class Converter(val accountId:Int) extends Function[String, String]  {
        
        // copy and send body from here -->>
        override def apply(message: String): String = {
          printMe()
          message.reverse
        }
        def printMe(): Unit = {
          println("Executing Function: reverse String")
        }
        // <<-- copy sent body up to here
        
      }