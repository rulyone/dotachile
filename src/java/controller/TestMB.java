/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class TestMB {

    /** Creates a new instance of TestMB */
    public TestMB() {
    }

    public void handler(FileUploadEvent e) {
        System.out.println("PASE POR ACA");
    }
    
}
