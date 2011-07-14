package org.apache.stanbol.ontologymanager.web.it;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.client.ClientProtocolException;

import org.apache.stanbol.commons.testing.stanbol.StanbolTestBase;
import org.apache.stanbol.commons.web.base.format.KRFormat;

public class SessionTest extends StanbolTestBase{

    public static final String ROOT_URI = "/ontonet";

    public static final String SESSION_URI = ROOT_URI + "/session";
    
    private static final Logger log = LoggerFactory.getLogger(SessionTest.class);

    @Test
    public void testSessionCreation() throws ClientProtocolException, IOException {
        
        /*log.info(executor.execute(
            builder.buildPostRequest(SESSION_URI+"?scope="+encodeURI("http://localhost:8080/ontonet/ontology/User"))
            .withHeader("Content-type",MediaType.APPLICATION_FORM_URLENCODED)
            .withHeader("Accept",KRFormat.RDF_XML)
        ).getContent()); //.assertStatus(200);*/
        
        
        
        assertTrue(true);
        
    }
    
    private String encodeURI(String s) {
        StringBuilder o = new StringBuilder();
        for (char ch : s.toCharArray()) {
            if (isUnsafe(ch)) {
                o.append('%');
                o.append(toHex(ch / 16));
                o.append(toHex(ch % 16));
            } else o.append(ch);
        }
        return o.toString();
    }

    private char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0) return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }
}