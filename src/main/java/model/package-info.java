@XmlSchema(
        namespace = "http://uniquNamespace/deduce",
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns={@XmlNs(prefix="", namespaceURI="http://uniquNamespace/deduce")})
package model;


import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;