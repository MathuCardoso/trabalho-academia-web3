package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusMatricula;
import br.edu.ifpr.academia.exceptions.ApiException;
import br.edu.ifpr.academia.repositories.MatriculaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunaService alunaService;
    private final TreinoService treinoService;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            AlunaService alunaService,
            TreinoService treinoService
    ) {
        this.matriculaRepository = matriculaRepository;
        this.alunaService = alunaService;
        this.treinoService = treinoService;
    }

    public List<Matricula> listarTodas() {
        atualizarMatriculasVencidas();
        return matriculaRepository.findAll();
    }

    public Matricula buscarPorId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Matricula nao encontrada"));

        return atualizarStatusSeVencida(matricula);
    }

    public boolean pertenceAAlunaDoUsuario(Long matriculaId, String login) {
        return matriculaRepository.findById(matriculaId)
                .map(Matricula::getAluna)
                .map(Aluna::getId)
                .filter(alunaId -> alunaService.pertenceAoUsuario(alunaId, login))
                .isPresent();
    }

    public boolean pertenceAProfessoraDoUsuario(Long matriculaId, String login) {
        return matriculaRepository.findById(matriculaId)
                .map(Matricula::getTreino)
                .map(Treino::getId)
                .filter(treinoId -> treinoService.pertenceAProfessoraDoUsuario(treinoId, login))
                .isPresent();
    }

    public List<Matricula> listarPorAluna(Long alunaId) {
        atualizarMatriculasVencidas();
        return matriculaRepository.findByAlunaId(alunaId);
    }

    public List<Matricula> listarPorProfessora(Long professoraId) {
        atualizarMatriculasVencidas();
        return matriculaRepository.findByTreinoProfessoraId(professoraId);
    }

    public Matricula salvar(Matricula matricula) {
        matricula.setAluna(buscarAlunaObrigatoria(matricula));
        matricula.setTreino(buscarTreinoObrigatorio(matricula));

        validarDatas(matricula);

        matricula.setStatus(calcularStatus(matricula));

        return matriculaRepository.save(matricula);
    }

    public Matricula atualizar(Long id, Matricula dadosAtualizados) {
        Matricula matricula = buscarPorId(id);

        matricula.setAluna(buscarAlunaObrigatoria(dadosAtualizados));
        matricula.setTreino(buscarTreinoObrigatorio(dadosAtualizados));
        matricula.setDataInicio(dadosAtualizados.getDataInicio());
        matricula.setDataVencimento(dadosAtualizados.getDataVencimento());
        matricula.setStatus(dadosAtualizados.getStatus());

        validarDatas(matricula);

        matricula.setStatus(calcularStatus(matricula));

        return matriculaRepository.save(matricula);
    }

    public void excluir(Long id) {
        Matricula matricula = buscarPorId(id);
        matriculaRepository.delete(matricula);
    }

    public List<Matricula> listarVencidas() {
        atualizarMatriculasVencidas();
        return matriculaRepository.findByStatus(StatusMatricula.VENCIDA);
    }

    public List<Matricula> listarAVencer() {
        atualizarMatriculasVencidas();

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        return matriculaRepository.findByStatusAndDataVencimentoBetween(
                StatusMatricula.ATIVA,
                hoje,
                limite
        );
    }

    public Matricula cancelar(Long id) {
        Matricula matricula = buscarPorId(id);
        matricula.setStatus(StatusMatricula.CANCELADA);
        return matriculaRepository.save(matricula);
    }

    public boolean alunaPossuiMatriculaAtiva(Long alunaId) {
        atualizarMatriculasVencidas();

        return matriculaRepository.existsByAlunaIdAndStatusAndDataVencimentoGreaterThanEqual(
                alunaId,
                StatusMatricula.ATIVA,
                LocalDate.now()
        );
    }

    public void validarAlunaPossuiMatriculaAtiva(Long alunaId) {
        boolean possuiMatriculaAtiva = alunaPossuiMatriculaAtiva(alunaId);

        if (!possuiMatriculaAtiva) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Aluna nao possui matricula ativa ou a matricula esta vencida",
                    "aluna"
            );
        }
    }

    public void atualizarMatriculasVencidas() {
        List<Matricula> matriculasVencidas = matriculaRepository.findByStatusAndDataVencimentoBefore(
                StatusMatricula.ATIVA,
                LocalDate.now()
        );

        if (matriculasVencidas.isEmpty()) {
            return;
        }

        matriculasVencidas.forEach(matricula -> matricula.setStatus(StatusMatricula.VENCIDA));
        matriculaRepository.saveAll(matriculasVencidas);
    }

    private Matricula atualizarStatusSeVencida(Matricula matricula) {
        if (
                matricula.getStatus() == StatusMatricula.ATIVA
                        && matricula.getDataVencimento() != null
                        && matricula.getDataVencimento().isBefore(LocalDate.now())
        ) {
            matricula.setStatus(StatusMatricula.VENCIDA);
            return matriculaRepository.save(matricula);
        }

        return matricula;
    }

    private StatusMatricula calcularStatus(Matricula matricula) {
        if (matricula.getStatus() == StatusMatricula.CANCELADA) {
            return StatusMatricula.CANCELADA;
        }

        if (matricula.getDataVencimento().isBefore(LocalDate.now())) {
            return StatusMatricula.VENCIDA;
        }

        return StatusMatricula.ATIVA;
    }

    private void validarDatas(Matricula matricula) {
        if (matricula.getDataInicio() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A data de inicio e obrigatoria", "dataInicio");
        }

        if (matricula.getDataVencimento() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A data de vencimento e obrigatoria", "dataVencimento");
        }

        if (matricula.getDataVencimento().isBefore(matricula.getDataInicio())) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "A data de vencimento nao pode ser anterior a data de inicio",
                    "dataVencimento"
            );
        }
    }

    private Aluna buscarAlunaObrigatoria(Matricula matricula) {
        if (matricula.getAluna() == null || matricula.getAluna().getId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A aluna e obrigatoria", "aluna");
        }

        return alunaService.buscarPorId(matricula.getAluna().getId());
    }

    private Treino buscarTreinoObrigatorio(Matricula matricula) {
        if (matricula.getTreino() == null || matricula.getTreino().getId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "O treino e obrigatorio", "treino");
        }

        return treinoService.buscarPorId(matricula.getTreino().getId());
    }
}
