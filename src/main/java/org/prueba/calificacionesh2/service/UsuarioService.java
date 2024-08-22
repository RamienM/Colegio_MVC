package org.prueba.calificacionesh2.service;


import org.prueba.calificacionesh2.dto.UsuarioRegisterDTO;
import org.prueba.calificacionesh2.entity.Usuario;
import org.prueba.calificacionesh2.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario loadUserByUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent()) return usuario.get();
        return null;
    }

    public Usuario registrarUsuario(UsuarioRegisterDTO usuario) {
        Usuario user = new Usuario();
        user.setNombre(usuario.getNombre());
        user.setApellido(usuario.getApellido());
        user.setCorreo(usuario.getCorreo());
        user.setTelefono(usuario.getTelefono());
        user.setUsername(usuario.getUsername());
        user.setPassword(usuario.getPassword());
        user.setRol(usuario.getRol());
        return usuarioRepository.save(user);
    }
}
