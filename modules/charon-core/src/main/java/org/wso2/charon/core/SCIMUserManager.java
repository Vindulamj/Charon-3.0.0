/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon.core;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.wso2.charon.core.attributes.AbstractAttribute;
import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.attributes.ComplexAttribute;
import org.wso2.charon.core.attributes.SimpleAttribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.utils.codeutils.ExpressionNode;
import org.wso2.charon.core.utils.codeutils.Node;
import org.wso2.charon.core.utils.codeutils.OperationNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SCIMUserManager implements UserManager {

    public static final String USER_NAME_STRING = "userName";
    public static final String SCIM_ENABLED = "SCIMEnabled";
    public static final String APPLICATION_DOMAIN = "Application";
    public static final String INTERNAL_DOMAIN = "Internal";
    private static Log log = LogFactory.getLog(SCIMUserManager.class);


    public SCIMUserManager() {
    }

    public User createUser(User user) throws CharonException {
        //TODO: Get the E-Tag(version) and add as a attribute of the cretated user
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("/home/vindula/Desktop/Charon/Storage/"+user.getId()+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /home/vindula/Desktop/Charon/Storage/"+user.getId()+".ser\n\n");
        }catch(IOException i) {
            i.printStackTrace();
        }
        return user;
    }

    @Override

    public User getUser(String id) {

        User e = null;
        try {
            FileInputStream fileIn = new FileInputStream("/home/vindula/Desktop/Charon/Storage/"+id+".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (User) in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i) {
            return null;
        }catch(ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return null;
        }
        return e;
    }

    @Override
    public void deleteUser(String userId) throws NotFoundException, CharonException {
        try{
            File file = new File("/home/vindula/Desktop/Charon/Storage/"+userId+".ser");

            if(file.delete()){
                System.out.println(file.getName() + " is deleted!");
            }else {
                throw new CharonException("Error occurred while deleting");

            }
        }catch(Exception e){
            throw new NotFoundException();
        }
    }

    @Override
    public List<User> listUsers() throws CharonException {
        final File folder = new File("/home/vindula/Desktop/Charon/Storage/");
        List<User> userList=new ArrayList<User>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                User e = null;
                try {
                    FileInputStream fileIn = new FileInputStream("/home/vindula/Desktop/Charon/Storage/"+fileEntry.getName());
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    e = (User) in.readObject();
                    in.close();
                    fileIn.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                userList.add(e);
            }

            }
        return userList;
    }

    @Override
    public List<User> listWithPagination(int startIndex, int count) {
        final File folder = new File("/home/vindula/Desktop/Charon/Storage/");
        List<User> userList=new ArrayList<User>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                User e = null;
                try {
                    FileInputStream fileIn = new FileInputStream("/home/vindula/Desktop/Charon/Storage/"+fileEntry.getName());
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    e = (User) in.readObject();
                    in.close();
                    fileIn.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                userList.add(e);
            }

        }
        List<User> userListNew=new ArrayList<User>();
        for(int i=startIndex-1;i<startIndex-1+count;i++){
            userListNew.add(userList.get(i));
        }
        return userListNew;
    }

    @Override
    public int getUserCount() {
        try {
            return listUsers().size();
        } catch (CharonException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public User updateUser(User validatedUser) {
        try {
            User user=createUser(validatedUser);
            return user;
        } catch (CharonException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> filterUsers(Node rootNode) {
        System.out.println(((ExpressionNode)(rootNode).getRightNode().getRightNode().getRightNode()).getAttributeValue());
/*
        ExpressionNode en=(ExpressionNode)rootNode;
        String attributeValue = en.getAttributeValue();
        String operation  = en.getOperation();
        String value= en.getValue();
        System.out.println(attributeValue);
        try {
            List<User> list= listUsers();
            List<User> newList =new ArrayList<User>();
            for(User user:list){
                Map<String, Attribute> attributeList= user.getAttributeList();
                Attribute checkAttribute = attributeList.get("userName");
                if(checkAttribute != null){
                    if (((SimpleAttribute)checkAttribute).getValue().equals(value)){
                        newList.add(user);
                    }
                }
            }
            return newList;
        } catch (CharonException e) {
            e.printStackTrace();
        }
*/
        return null;
    }


}


