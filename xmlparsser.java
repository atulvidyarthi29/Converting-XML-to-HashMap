import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings("unchecked")
class XMLParsing {
    public static Document convertXMLFileToXMLDocument(String filePath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            return doc;
        } catch (Exception e) {
            System.out.println("Error occured parsing the file.");
        }
        return null;
    }

    public static <T> HashMap<String, T> utilXML(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element elem = (Element) node;
            NodeList children = elem.getChildNodes();
            HashMap<String, T> hashMap = new HashMap<>();
            int length = children.getLength();
            for (int i = 0; i < length; i++) {
                Node child = children.item(i);
                if (length == 1 && child instanceof Text) {
                    HashMap<String, T> ob = new HashMap<>();
                    ob.put(child.getNodeName(), (T) child.getTextContent());
                    return ob;
                }
                hashMap.put(child.getNodeName(), (T) utilXML(child));
            }
            return hashMap;
        }
        return null;
    }

    public static <U> HashMap<String, HashMap<String, U>> transformXML(String xmlFilePath) {
        Document doc = convertXMLFileToXMLDocument(xmlFilePath);
        doc.getDocumentElement().normalize();
        Node node = doc.getDocumentElement();
        HashMap<String, HashMap<String, U>> tree = new HashMap<>();
        tree.put(node.getNodeName(), (HashMap<String, U>) utilXML(node));
        return tree;
    }

    public static <V> HashMap<String, V> evalXML(HashMap<String, V> tree, String keys) {
        String[] splited = keys.split("\\s+");
        HashMap<String, V> iter = tree;
        for (String key : splited) {
            try {
                iter = (HashMap<String, V>) iter.get(key);
            } catch (NullPointerException n) {
                return null;
            }
        }
        return iter;
    }

    public static <V> void main(String[] args) {
        Scanner ob = new Scanner(System.in);
        System.out.println("Enter the path of the file:");
        final String xmlFilePath = ob.nextLine();
        HashMap<String, V> tree = (HashMap<String, V>) transformXML(xmlFilePath);
        System.out.println("\nEnter keys in order:");
        String keys = ob.nextLine();
        HashMap<String, V> results = evalXML(tree, keys);
        if (results == null) {
            System.out.println("Couldn't find anything!");
        } else
            System.out.println("\n" +results);
        ob.close();
    }
}


/*
sample input-
filename = d.xml
keys = B2C-REPORT ACCOUNTS-SUMMARY PRIMARY-ACCOUNTS-SUMMARY PRIMARY-OVERDUE-NUMBER-OF-ACCOUNTS

*/