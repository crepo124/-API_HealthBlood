/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.model.Medida;

/**
 *
 * @author vdjun
 */
public class ControllerMedida {
    private static final DateTimeFormatter FECHA_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Medida> getMedidas() throws SQLException {
        List<Medida> lista = new ArrayList<>();
        String sql = "SELECT * FROM medida";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(fillMedida(rs));
            }
        } finally {
            connMysql.close();
        }
        return lista;
    }

    public List<Medida> getMedidasByUsuario(int idUsuario) throws SQLException {
        List<Medida> lista = new ArrayList<>();
        String sql = "SELECT * FROM medida WHERE idUsuario = ?";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idUsuario);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    lista.add(fillMedida(rs));
                }
            }
        } finally {
            connMysql.close();
        }
        return lista;
    }

    public Medida insertMedida(Medida medida) throws SQLException {
        String sql = "INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas) "
                + "VALUES (?, ?, NOW(), ?, ?)";
        int idGenerado = 0;
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());
            pstm.setString(3, medida.getContexto());
            pstm.setString(4, medida.getNotas());
            pstm.executeUpdate();
            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    medida.setIdMedida(rs.getInt(1));
                    idGenerado = medida.getIdMedida();
                }
            }
        } finally {
            connMysql.close();
        }
        if (idGenerado > 0) {
            Medida creada = getMedidaById(idGenerado);
            if (creada != null) {
                return creada;
            }
        }
        return medida;
    }

    private Medida getMedidaById(int idMedida) throws SQLException {
        String sql = "SELECT * FROM medida WHERE idMedida = ?";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idMedida);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return fillMedida(rs);
                }
            }
        } finally {
            connMysql.close();
        }
        return null;
    }

    public Medida fillMedida(ResultSet rs) throws SQLException {
        Medida medida = new Medida();
        medida.setIdMedida(rs.getInt("idMedida"));
        medida.setIdUsuario(rs.getInt("idUsuario"));
        medida.setGlucosa(rs.getInt("glucosa"));
        Timestamp ts = rs.getTimestamp("fechaHora");
        medida.setFechaHora(ts != null ? ts.toLocalDateTime().format(FECHA_FORMAT) : null);
        medida.setContexto(rs.getString("contexto"));
        medida.setNotas(rs.getString("notas"));
        return medida;
    }
}
