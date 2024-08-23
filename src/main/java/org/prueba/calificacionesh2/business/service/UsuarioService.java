package org.prueba.calificacionesh2.business.service;


import org.prueba.calificacionesh2.business.dto.UsuarioRegisterDTO;
import org.prueba.calificacionesh2.persistence.entity.Usuario;
import org.prueba.calificacionesh2.persistence.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario loadUserByUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.orElse(null);
    }

    public void registrarUsuario(UsuarioRegisterDTO usuario) {
        Usuario user = new Usuario();
        user.setNombre(usuario.getNombre());
        user.setApellido(usuario.getApellido());
        user.setCorreo(usuario.getCorreo());
        user.setTelefono(usuario.getTelefono());
        user.setUsername(usuario.getUsername());
        user.setPassword(usuario.getPassword());
        user.setRol(usuario.getRol());
        usuarioRepository.save(user);
    }
}
