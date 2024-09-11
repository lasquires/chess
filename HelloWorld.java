public class HelloWorld {
    public static void main(String[] args){
        System.out.println("Hello World!");

        var p = new Person("James");
        p.sleep();
        System.out.println("\n");
        String[] names = {"fee", "fi", "fo", "fum"};
        for (var i =0; i < names.length; i++){
            System.out.println(names[i]);
        }
    }
}

