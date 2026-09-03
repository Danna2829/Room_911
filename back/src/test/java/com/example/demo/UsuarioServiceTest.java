package com.example.demo;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.PerfilOperario;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EmpleadoRepository;
import com.example.demo.repository.PerfilOperarioRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private PerfilOperarioRepository perfilRepo;

    @Mock
    private RolRepository rolRepo;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private EmpleadoRepository empleadoRepo;

    @Test
    @DisplayName("Crear Operario genera formato EMP-XXXX y persiste PerfilOperario con nivel ABAC")
    void testCrearUsuarioOperario() {
        UsuarioDto dto = new UsuarioDto();
        dto.setNombre("Carlos");
        dto.setApellido("Mendoza");
        dto.setCorreo("carlos@farmaceutica.com");
        dto.setRol("OPERARIO");
        dto.setNivelAcceso(2);

        when(rolRepo.findByNombre("OPERARIO")).thenReturn(Optional.of(new Rol("OPERARIO")));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepo.existsById(any())).thenReturn(false);
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(perfilRepo.findByIdUsuario(any())).thenReturn(Optional.empty());

        UsuarioDto result = usuarioService.crearUsuario(dto);

        assertNotNull(result.getIdUsuario());
        assertTrue(result.getIdUsuario().startsWith("EMP-"));
        assertEquals(2, result.getNivelAcceso());
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
        verify(perfilRepo, times(1)).save(any(PerfilOperario.class));
    }

    @Test
    @DisplayName("Listar Usuarios incluye nivelAcceso ABAC")
    void testListarUsuarios() {
        Usuario u1 = new Usuario();
        u1.setIdUsuario("EMP-8821");
        u1.setNombre("Juan");
        u1.setRol("OPERARIO");

        when(usuarioRepo.findAll()).thenReturn(List.of(u1));
        when(perfilRepo.findByIdUsuario("EMP-8821")).thenReturn(Optional.of(new PerfilOperario("EMP-8821", 1, "Nivel 1")));

        List<UsuarioDto> lista = usuarioService.listarUsuarios();

        assertEquals(1, lista.size());
        assertEquals("EMP-8821", lista.get(0).getIdUsuario());
        assertEquals(1, lista.get(0).getNivelAcceso());
    }

    @Test
    @DisplayName("Inhabilitar Usuario ejecuta Soft Delete (estado = false) sin borrar físicamente de BD")
    void testSoftDeleteUsuario() {
        Usuario u = new Usuario();
        u.setIdUsuario("EMP-8821");
        u.setEstado(true);

        when(usuarioRepo.findById("EMP-8821")).thenReturn(Optional.of(u));
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        usuarioService.eliminarUsuario("EMP-8821");

        assertFalse(u.getEstado());
        verify(usuarioRepo, times(1)).save(u);
        verify(usuarioRepo, never()).deleteById(any());
        verify(usuarioRepo, never()).delete(any());
    }
}
