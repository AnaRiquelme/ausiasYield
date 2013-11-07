/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.daw.parameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import net.daw.bean.RepositorioBean;

/**
 *
 * @author Alvaro
 */
public class RepositorioParam {

    private HttpServletRequest request;

    public RepositorioParam(HttpServletRequest request) throws Exception {
        this.request = request;
    }

    public RepositorioBean loadId(RepositorioBean oRepositorioBean) throws NumberFormatException {
        try {
            if (request.getParameter("id") != null) {
                oRepositorioBean.setId(Integer.parseInt(request.getParameter("id")));
            } else {
                oRepositorioBean.setId(0);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Controller: Error: Load: Formato de datos en parámetros incorrecto " + e.getMessage());
        }
        return oRepositorioBean;
    }

    public RepositorioBean load(RepositorioBean oRepositorioBean) throws NumberFormatException {
        try {
            if ((request.getParameter("titulo") != null)) {
                oRepositorioBean.setTitulo(request.getParameter("nombre"));
            }
            if ((request.getParameter("contenido") != null)) {
                oRepositorioBean.setContenido(request.getParameter("ape1"));
            }
            if ((request.getParameter("fecha") != null)) {
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                oRepositorioBean.setFecha(formato.parse(request.getParameter("ape2")));
            }
            /*if ((request.getParameter("email") != null)) {
             oRepositorioBean.setEmail(request.getParameter("email"));
             }*/
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Controller: Error: Load: Formato de datos en parámetros incorrecto " + e.getMessage());
        } catch (ParseException ex) {
            Logger.getLogger(RepositorioParam.class.getName()).log(Level.SEVERE, null, ex);
        }
        return oRepositorioBean;
    }

}