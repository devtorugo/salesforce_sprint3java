package fiap.challenge.sprin3.repositories;

import fiap.challenge.sprin3.infrastructure.OracleDbConfiguration;
import fiap.challenge.sprin3.entities.Cadastro;
import fiap.challenge.sprin3.entities.Regiao;
import fiap.challenge.sprin3.entities.Termo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CadastroRepository {
    private static final Logger logger = LogManager.getLogger(CadastroRepository.class);
    public static final String TB_NAME = "CADASTRO";

    public List<Cadastro> getAll() {
        var cadastros = new ArrayList<Cadastro>();
        try (var conn = OracleDbConfiguration.getConnection();
             var stmt = conn.prepareStatement("SELECT * FROM " + TB_NAME + " ORDER BY ID")) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                cadastros.add(new Cadastro(
                        rs.getInt("ID"),
                        rs.getString("NOME"),
                        rs.getString("TELEFONE"),
                        rs.getString("EMAIL"),
                        rs.getString("SENHA"),
                        rs.getString("EMPRESA"),
                        rs.getString("IDIOMA"),
                        getRegiaoById(rs.getInt("REGIAO_ID")),
                        getTermoById(rs.getInt("TERMO_ID"))));
            }
        }
        catch (SQLException e) {
            logger.error("Erro ao buscar todos os cadastros", e);
        }
        return cadastros;
    }

    public Optional<Cadastro> get(int id) {
        try (Connection conn = OracleDbConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + TB_NAME + " WHERE ID = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cadastro cadastro = new Cadastro(
                        rs.getInt("ID"),
                        rs.getString("NOME"),
                        rs.getString("TELEFONE"),
                        rs.getString("EMAIL"),
                        rs.getString("SENHA"),
                        rs.getString("EMPRESA"),
                        rs.getString("IDIOMA"),
                        getRegiaoById(rs.getInt("REGIAO_ID")),
                        getTermoById(rs.getInt("TERMO_ID"))
                );
                return Optional.of(cadastro);
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar cadastro com ID: " + id, e);
        }
        return Optional.empty();
    }

    public void create(Cadastro cadastro) {
        try (Connection conn = OracleDbConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TB_NAME + " (NOME, TELEFONE, EMAIL, SENHA, EMPRESA, IDIOMA) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, cadastro.getNOME());
            stmt.setString(2, cadastro.getTELEFONE());
            stmt.setString(3, cadastro.getEMAIL());
            stmt.setString(4, cadastro.getSENHA());
            stmt.setString(5, cadastro.getEMPRESA());
            stmt.setString(6, cadastro.getIDIOMA());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao criar cadastro", e);
        }
    }

    public void update(int id, Cadastro cadastro) {
        try (Connection conn = OracleDbConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE " + TB_NAME + " SET NOME = ?, TELEFONE = ?, EMAIL = ?, SENHA = ?, EMPRESA = ?, IDIOMA = ? WHERE ID = ?")) {
            stmt.setString(1, cadastro.getNOME());
            stmt.setString(2, cadastro.getTELEFONE());
            stmt.setString(3, cadastro.getEMAIL());
            stmt.setString(4, cadastro.getSENHA());
            stmt.setString(5, cadastro.getEMPRESA());
            stmt.setString(6, cadastro.getIDIOMA());
            stmt.setInt(7, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao atualizar cadastro com ID: " + id, e);
        }
    }

    public Regiao getRegiaoById(int regiaoId) {
        Regiao regiao = null;
        String query = "SELECT * FROM REGIAO WHERE ID = ?";
        try (Connection conn = OracleDbConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, regiaoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                regiao = new Regiao(
                        rs.getInt("ID"),
                        rs.getString("PAIS_NOME")
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar região por ID: " + regiaoId, e);
        }
        return regiao;
    }

    public Termo getTermoById(int termoId) {
        Termo termo = null;
        String query = "SELECT * FROM TERMO WHERE ID = ?";
        try (Connection conn = OracleDbConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, termoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                termo = new Termo(
                        rs.getInt("ID"),
                        rs.getBoolean("ACEITAR_TERMO")
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar termo por ID: " + termoId, e);
        }
        return termo;
    }

    public void delete(int id) {
        try (Connection conn = OracleDbConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + TB_NAME + " WHERE ID = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao deletar cadastro com ID: " + id, e);
        }
    }
}
