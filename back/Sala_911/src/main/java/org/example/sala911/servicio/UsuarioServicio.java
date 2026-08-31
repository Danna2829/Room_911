package org.example.sala911.servicio;

import org.example.sala911.dto.UsuarioRespuesta;
import org.example.sala911.entidad.*;
import org.example.sala911.repositorio.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioServicio {
    private final UsuarioRepositorio usuarios; private final PerfilRepositorio perfiles;
    public UsuarioServicio(UsuarioRepositorio usuarios,PerfilRepositorio perfiles){this.usuarios=usuarios;this.perfiles=perfiles;}
    public List<UsuarioRespuesta> todos(){return usuarios.findAll().stream().map(this::respuesta).toList();}
    public Usuario buscar(String id){return usuarios.findById(id).orElseThrow(()->new IllegalArgumentException("Usuario no encontrado"));}
    public UsuarioRespuesta identificar(String id){return respuesta(buscar(id));}
    public Usuario guardar(Usuario usuario){ if(usuario.getPerfil()==null) usuario.setPerfil(perfiles.findByNivel(1).orElseThrow()); return usuarios.save(usuario); }
    public Usuario actualizarEstado(String id, boolean activo, LocalDate desde, LocalDate hasta){Usuario u=buscar(id);u.setActivo(activo);u.setSuspendidoDesde(desde);u.setSuspendidoHasta(hasta);return usuarios.save(u);}
    public UsuarioRespuesta respuesta(Usuario u){Perfil p=u.getPerfil();return new UsuarioRespuesta(u.getIdUsuario(),u.getNombre(),u.getApellido(),u.getCorreo(),u.getRol(),p.getNivel(),p.getNombre(),u.isActivo(),u.getSuspendidoDesde(),u.getSuspendidoHasta());}
}
