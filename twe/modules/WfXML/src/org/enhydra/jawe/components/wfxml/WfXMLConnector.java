/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see http://www.gnu.org/licenses
 */

/*
 * Created on Dec 28, 2004
 */
package org.enhydra.jawe.components.wfxml;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.elements.Package;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author V.Puskas
 */
public class WfXMLConnector {

   private static final String REQUEST_METHOD = "POST";

   private static final String HRP_ACCEPT_NAME = "Accept";

   private static final String HTTP_ACCEPT_VALUE = "application/soap+xml, text/*";

   private static final String HRP_USER_AGENT_NAME = "User-Agent";

   private static final String HRP_CACHE_CONTROL_NAME = "Cache-Control";

   private static final String HRP_PRAGMA_NAME = "Pragma";

   private static final String HRP_CONTENT_TYPE_NAME = "Content-Type";

   private static final String HRP_SOAP_ACTION = "SOAPAction";

   private static final String HRP_USER_AGENT_VALUE = "JaWE/1.4.2";

   private static final String HRP_CACHE_CONTROL_VALUE = "no-cache";

   private static final String HRP_PRAGMA_VALUE = "no-cache";

   private static final String HRP_CONTENT_TYPE_VALUE = "text/xml; charset=utf-8";

   private static final String REQUEST_HEADER = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                                                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                                                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "<soapenv:Header>"
                                                + "<as:Request xmlns:as=\"http://www.oasis-open.org/asap/0.9/asap.xsd\">"
                                                + "<as:SenderKey>http://jawe.objectweb.org/</as:SenderKey>" + "<as:ReceiverKey>" + "${1}" + "</as:ReceiverKey>"
                                                + "</as:Request>" + "</soapenv:Header>";

   static {
      // for localhost testing only
      javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

         public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            return true;
         }
      });
   }

   public static void main(String[] args) {
      try {
         URL url = new URL("http://grunf:8080/axis/services/wfxmlRegistryBinding");
         wfxmlSetDefinition(url, "/tmp/asap_test.xpdl");
         /*List l = wfxmlListDefinitions(url);
          for (Iterator it = l.iterator(); it.hasNext();) {
          url = (URL) it.next();
          System.err.println("##" + url);
          wfxmlGetDefinition(url);
          }*/
         // wfxmlSetDefinition(url, "/tmp/Application_Repository.xpdl");
         // wfxmlSetDefinition(url, "/tmp/test-JavaScript.xpdl");
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   public static boolean wfxmlSetDefinition(URL url, String fullFilename) throws Exception {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/factory/SetDefinition");
      FileReader fr = new FileReader(fullFilename);
      bos.println("<soapenv:Body>" + "<SetDefinitionRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\">" + "<ProcessLanguage>XPDL</ProcessLanguage>" + "<Definition>");
      BufferedReader br = new BufferedReader(fr);
      br.readLine();
      while (br.ready()) {
         bos.println(br.readLine());
      }
      bos.println("</Definition>" + "</SetDefinitionRq>" + "</soapenv:Body>" + "</soapenv:Envelope>");
      bos.flush();
      br.close();
      fr.close();
      conn.connect();
      int response = conn.getResponseCode();

      bos.close();
      conn.disconnect();

      return isResponse2XX(response);
   }

   public static void wfxmlSetDefinition2(URL url, String pkgCnt) throws Exception {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/factory/SetDefinition");
      bos.println("<soapenv:Body>" + "<SetDefinitionRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\">" + "<ProcessLanguage>XPDL</ProcessLanguage>" + "<Definition>");
      bos.println(pkgCnt);
      bos.println("</Definition>" + "</SetDefinitionRq>" + "</soapenv:Body>" + "</soapenv:Envelope>");
      bos.flush();
      conn.connect();
      int response = conn.getResponseCode();
      bos.close();
      conn.disconnect();

      if (!isResponse2XX(response)) {
         throw new Exception("Invalid response " + response);
      }
   }

   public static boolean wfxmlNewDefinition(URL url, String fullFilename) throws Exception {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/registry/NewDefinition");
      FileReader fr = new FileReader(fullFilename);
      bos.println("<soapenv:Body>" + "<NewDefinitionRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\">" + "<ProcessLanguage>XPDL</ProcessLanguage>" + "<Definition>");
      BufferedReader br = new BufferedReader(fr);
      br.readLine();
      while (br.ready()) {
         bos.println(br.readLine());
      }
      bos.println("</Definition>" + "</NewDefinitionRq>" + "</soapenv:Body>" + "</soapenv:Envelope>");
      bos.flush();
      br.close();
      fr.close();
      conn.connect();
      int response = conn.getResponseCode();
      bos.close();
      conn.disconnect();

      return isResponse2XX(response);
   }

   public static void wfxmlNewDefinition2(URL url, String pkgCnt) throws Exception {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/registry/NewDefinition");
      bos.println("<soapenv:Body>" + "<NewDefinitionRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\">" + "<ProcessLanguage>XPDL</ProcessLanguage>" + "<Definition>");
      bos.println(pkgCnt);
      bos.println("</Definition>" + "</NewDefinitionRq>" + "</soapenv:Body>" + "</soapenv:Envelope>");
      bos.flush();
      conn.connect();
      int response = conn.getResponseCode();
      bos.close();
      conn.disconnect();

      if (!isResponse2XX(response)) {
         throw new Exception("Invalid response " + response);
      }

   }

   public static Node wfxmlGetDefinition(URL url) throws Exception {
      Node ret = null;

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/factory/GetDefinition");
      bos.println("<soapenv:Body>"
                  + "<GetDefinitionRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\">" + "<ProcessLanguage>XPDL</ProcessLanguage>" + "</GetDefinitionRq>"
                  + "</soapenv:Body>" + "</soapenv:Envelope>");
      bos.flush();
      conn.connect();
      int response = conn.getResponseCode();
      conn.getContentLength();
      if (isResponse2XX(response)) {
         Document b = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
         System.err.println(b.getNodeName());
         System.err.println(b.getFirstChild().getNodeName());
         System.err.println(b.getFirstChild().getLastChild().getFirstChild().getFirstChild());
         ret = b.getFirstChild().getLastChild().getFirstChild().getFirstChild();
         bos.close();
         conn.disconnect();
         return ret;
      }

      bos.close();
      conn.disconnect();
      throw new Exception("Invalid response " + response);
   }

   public static String wfxmlGetDefinition2(URL url) throws Exception {
      Node ret = wfxmlGetDefinition(url);
      return node2String(ret);
   }

   public static List wfxmlListDefinitions(URL url) throws Exception {
      List ret = new ArrayList();

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/factory/ListDefinitions");
      bos.println("<soapenv:Body>" + "<ListDefinitionsRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\"/>" + "</soapenv:Body> </soapenv:Envelope>");
      bos.flush();
      conn.connect();
      int response = conn.getResponseCode();

      if (isResponse2XX(response)) {
         Document b = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
         NodeList f = b.getFirstChild().getLastChild().getFirstChild().getChildNodes();
         for (int i = 0; i < f.getLength(); ++i) {
            ret.add(new URL(f.item(i).getFirstChild().getFirstChild().getNodeValue()));
         }
         return ret;
      }

      throw new Exception("Invalid response " + response);
   }

   public static List wfxmlListDefinitions(URL url, DefInfos dis) throws Exception {
      List ret = new ArrayList();

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      PrintWriter bos = openConnection(conn, "http://www.wfmc.org/wfxml/2.0/wfxml/factory/ListDefinitions");
      bos.println("<soapenv:Body>" + "<ListDefinitionsRq xmlns=\"http://www.wfmc.org/wfxml/2.0/\"/>" + "</soapenv:Body> </soapenv:Envelope>");
      bos.flush();
      conn.connect();
      int response = conn.getResponseCode();

      if (isResponse2XX(response)) {
         Document b = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
         NodeList f = b.getFirstChild().getLastChild().getFirstChild().getChildNodes();
         for (int i = 0; i < f.getLength(); ++i) {
            NodeList nl = f.item(i).getChildNodes();
            String dk = nl.item(0).getFirstChild().getNodeValue();
            String n = "";
            if (nl.item(1).getFirstChild() != null) {
               n = nl.item(1).getFirstChild().getNodeValue();
            }
            String d = "";
            if (nl.item(2).getFirstChild() != null) {
               d = nl.item(2).getFirstChild().getNodeValue();
            }
            String v = "";
            if (nl.item(3).getFirstChild() != null) {
               v = nl.item(3).getFirstChild().getNodeValue();
            }
            String s = "";
            if (nl.item(4).getFirstChild() != null) {
               s = nl.item(4).getFirstChild().getNodeValue();
            }
            DefInfo di = (DefInfo) dis.generateNewElement();
            di.setDefinitionKey(dk);
            di.setName(n);
            di.setDescription(d);
            di.setVersion(v);
            di.setStatus(s);
            ret.add(di);
         }
         return ret;
      }

      throw new Exception("Invalid response " + response);
   }

   public static String xpdlToString(Package pkg) throws Exception {
      Document document = null;

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder dbuilder = dbf.newDocumentBuilder();
      document = dbuilder.newDocument();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      JaWEManager.getInstance().getXPDLHandler().getXPDLRepositoryHandler().toXML(document, pkg);

      // Use a Transformer for output
      TransformerFactory tFactory = new org.apache.xalan.processor.TransformerFactoryImpl();
      Transformer transformer = tFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty("encoding", JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding());
      transformer.setOutputProperty("omit-xml-declaration", "yes");
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(baos);
      transformer.transform(source, result);

      // String s = baos.toString(
      // JaWEManager.getInstance()
      // .getJaWEController()
      // .getControllerSettings()
      // .getEncoding());
      String s = baos.toString();
      // System.err.println("S="+s);
      baos.close();
      return s;
   }

   public static String node2String(Node n) throws Exception {
      byte[] ba = node2Bytes(n);
      String s = new String(ba, JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding());
      // System.out.println(s);
      return s;
   }

   public static byte[] node2Bytes(Node n) throws Exception {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      // Use a Transformer for output
      TransformerFactory tFactory = new org.apache.xalan.processor.TransformerFactoryImpl();
      Transformer transformer = tFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty("encoding", JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding());
      transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      DOMSource source = new DOMSource(n);
      StreamResult result = new StreamResult(baos);
      transformer.transform(source, result);

      byte[] ba = baos.toByteArray();
      // System.out.println(s);
      baos.close();
      return ba;
   }

   private static PrintWriter openConnection(HttpURLConnection conn, String action) throws Exception {
      conn.setRequestMethod(REQUEST_METHOD);
      conn.addRequestProperty(HRP_ACCEPT_NAME, HTTP_ACCEPT_VALUE);
      conn.addRequestProperty(HRP_USER_AGENT_NAME, HRP_USER_AGENT_VALUE);
      conn.addRequestProperty(HRP_CACHE_CONTROL_NAME, HRP_CACHE_CONTROL_VALUE);
      conn.addRequestProperty(HRP_PRAGMA_NAME, HRP_PRAGMA_VALUE);
      conn.addRequestProperty(HRP_CONTENT_TYPE_NAME, HRP_CONTENT_TYPE_VALUE);
      conn.addRequestProperty(HRP_SOAP_ACTION, action);
      conn.setDoOutput(true);
      String toPrint = REQUEST_HEADER.replaceFirst("\\$\\{1\\}", conn.getURL().toString());
      PrintWriter bos = new PrintWriter(conn.getOutputStream());
      bos.println(toPrint);
      return bos;
   }

   private static boolean isResponse2XX(int response) {
      return HttpURLConnection.HTTP_ACCEPTED == response || HttpURLConnection.HTTP_OK == response || HttpURLConnection.HTTP_CREATED == response;
   }

   public static void listNodes(Node m, String space) {
      NodeList nl = m.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
         Node n = nl.item(i);
         System.out.println(space + "i=" + i + ", n=" + n.getClass().getName() + ", v=" + n.getNodeValue());
         listNodes(n, space + " ");
      }
   }

}
