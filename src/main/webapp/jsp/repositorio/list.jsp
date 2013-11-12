
<%@page import="net.daw.helper.FilterBean"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="net.daw.bean.RepositorioBean"%>
<%@ page import="net.daw.helper.Contexto"%>
<%
    Contexto oContexto = (Contexto) request.getAttribute("contexto");
    ArrayList<Object> alObjetoParametro = (ArrayList<Object>) oContexto.getParametro();
    ArrayList<RepositorioBean> alPagina = (ArrayList<RepositorioBean>) alObjetoParametro.get(0);
    Iterator<RepositorioBean> oIterador = alPagina.listIterator();
%>
<div class="row-fluid">
    <div class="span9">
        <h1>Listado de repositorios</h1>
        <%
            if (!oIterador.hasNext()) {
                out.print("<h4>Listado vacío</h4>");
            } else {
        %>
        <%
            if (oContexto.getHmOrder() != null) {
                out.print("<p>Listado ordenado por " + oContexto.getHmOrder().keySet().toArray()[0].toString() + "    ");
                out.print("<a href=\"Controller?" + oContexto.getSerializedParamsExceptOrder() + "\">(Quitar orden)</a></p>");
            } else {
                out.print("<p>Sin ordenar</p>");
            }
        %>
        <%
            if (oContexto.getAlFilter() != null) {
                out.print("<p>Listado filtrado: ");
                ArrayList<FilterBean> alFilter = oContexto.getAlFilter();
                Iterator iterator = alFilter.iterator();
                while (iterator.hasNext()){
                    FilterBean oFilterBean = (FilterBean) iterator.next();
                    out.print("(" + oFilterBean.getFilter()+ " " + oFilterBean.getFilterOperator() + " " + oFilterBean.getFilterValue() + ") ");    
                }
                out.print("<a href=\"Controller?" + oContexto.getSerializedParamsExceptFilter() + "\">(Quitar filtro)</a></p>");
            } else {
                out.print("<p>Sin filtrar</p>");
            }
        %>
        <%
            ArrayList<String> paginacion = (ArrayList<String>) alObjetoParametro.get(1);
            Iterator<String> iterador2 = paginacion.listIterator();
            while (iterador2.hasNext()) {
                String o = iterador2.next();
                out.print(o);
            }
        %>
    </div>
    <div class="span3">
        <div class="text-right">
            <legend>Filtro de repositorio</legend> 
            <form class="navbar-form pull-right" action="Controller" method="post" id="repositorioForm">
                <fieldset>                                               
                    <%=oContexto.getSerializedParamsExceptFilterFormFormat()%>       
                    <span>
                        <select id="filter" name="filter" width="80" style="width: 80px">
                            <option>id</option>
                            <option>titulo</option>
                            <option>contenido</option>
                            <option>id_usuario</option>
                            <option>id_lenguaje</option>
                            <option>id_documento</option>
                            <option>fecha</option>
                        </select>                        
                        <input id="filtervalue" name="filtervalue" type="text" size="20" maxlength="50" value=""  width="100" style="width: 100px"/>
                    </span>
                    <span>
                        <input type="submit" name="enviar" value="Filtrar" />
                    </span>
                </fieldset>
        </div>
    </div>
</div>
<table class="table table-hover table-condensed">
    <tr>
        <th>id
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=id&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=id&ordervalue=desc"><i class="icon-arrow-down"></i></a>
        </th>
        <th>titulo
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=nombre&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=nombre&ordervalue=desc"><i class="icon-arrow-down"></i></a>
        </th>
        <th>contenido
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=ape1&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=ape1&ordervalue=desc"><i class="icon-arrow-down"></i></a>
        </th>
        <th>id_usuario
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=ape2&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=ape2&ordervalue=desc"><i class="icon-arrow-down"></i></a>        
        </th>
        <th>id_lenguaje
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=email&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=email&ordervalue=desc"><i class="icon-arrow-down"></i></a>         
        </th>
        <th>id_documento
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=email&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=email&ordervalue=desc"><i class="icon-arrow-down"></i></a>         
        </th>
        <th>fecha
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=email&ordervalue=asc"><i class="icon-arrow-up"></i></a>
            <a href="Controller?<%=oContexto.getSerializedParamsExceptOrder()%>&order=email&ordervalue=desc"><i class="icon-arrow-down"></i></a>         
        </th>
        <th>Operaciones</th>
    </tr>
    <%        while (oIterador.hasNext()) {
            RepositorioBean oRepositorioBean = oIterador.next();
            DateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    %>
    <tr>
        <td><%=oRepositorioBean.getId()%></td>
        <td><%=oRepositorioBean.getTitulo()%></td>
        <td><%=oRepositorioBean.getContenido()%></td>
        <td><%=oRepositorioBean.getId_usuario()%></td>
        <td><%=oRepositorioBean.getId_lenguaje()%></td>
        <td><%=oRepositorioBean.getId_documento()%></td>
        <td><%=formato.format(oRepositorioBean.getFecha())%></td>
        <td>
            <div class="btn-toolbar">
                <div class="btn-group">                    
                    <a class="btn btn-mini" href="Controller?class=repositorio&method=view&id=<%=oRepositorioBean.getId()%>"><i class="icon-eye-open"></i></a>                    
                    <a class="btn btn-mini" href="Controller?class=repositorio&method=update&id=<%=oRepositorioBean.getId()%>"><i class="icon-pencil"></i></a>           
                    <a class="btn btn-mini" href="Controller?class=repositorio&method=remove&id=<%=oRepositorioBean.getId()%>"><i class="icon-trash"></i></a>                         
                </div>
            </div>
        </td>
    </tr>
    <%
        }
    %>
</table>
<%
    }
%>
