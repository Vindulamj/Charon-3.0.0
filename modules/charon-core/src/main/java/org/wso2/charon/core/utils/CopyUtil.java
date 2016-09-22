package org.wso2.charon.core.utils;

import org.wso2.charon.core.exceptions.CharonException;

import java.io.*;

/**
 * Created by vindula on 9/21/16.
 */
public class CopyUtil {
    public static Object deepCopy(Object oldObject) throws CharonException {
        ObjectOutputStream objOutPutStream;
        ObjectInputStream objInputStream;
        Object newObject = null;
        try {

            //create byte array output stream
            ByteArrayOutputStream byteArrayOutPutStream = new ByteArrayOutputStream();
            //create object output stream using above
            objOutPutStream = new ObjectOutputStream(byteArrayOutPutStream);
            //serialize the object and write it to the byte array out put stream
            objOutPutStream.writeObject(oldObject);

            //create a byte array input stream from the content of the byte array output stream
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    byteArrayOutPutStream.toByteArray());

            objInputStream = new ObjectInputStream(byteArrayInputStream);
            newObject = objInputStream.readObject();

        }  catch (ClassNotFoundException e) {
            throw new CharonException("Error in de-serializing while creating a deep copy of the object");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newObject;
    }
}
