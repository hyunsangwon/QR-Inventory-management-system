package pro.cntech.inventory.util;

public class ContactFilter
{

    private static ContactFilter contactFilter;

    public static synchronized ContactFilter getInstance()
    {
        if(contactFilter == null)
        {
            contactFilter = new ContactFilter();
        }
        return contactFilter;
    }

    public String setPhoneNumber(String phone)
    {
        String firstNumber = null;
        String secondNumber = null;
        String lastNumber = null;

        if(phone.length() == 11)
        {
            firstNumber = phone.substring(0,3);
            secondNumber = phone.substring(3,7);
            lastNumber= phone.substring(7,11);
        }
        if(phone.length() == 9)
        {
            firstNumber = phone.substring(0,2);
            secondNumber = phone.substring(2,5);
            lastNumber= phone.substring(5,9);
        }
        if(phone.length() == 8)
        {
            firstNumber = phone.substring(0,4);
            secondNumber = phone.substring(4,8);
            return firstNumber+"-"+secondNumber;
        }
        if(phone.length() == 10)
        {
            firstNumber = phone.substring(0,3);
            secondNumber = phone.substring(3,6);
            lastNumber= phone.substring(6,10);
        }
        if(phone.length() > 11 || phone.length() < 8)
        {
            return phone;
        }

        return firstNumber+"-"+secondNumber+"-"+lastNumber;
    }

}
