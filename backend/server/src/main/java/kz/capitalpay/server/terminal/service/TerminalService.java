package kz.capitalpay.server.terminal.service;

import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.IdDto;
import kz.capitalpay.server.terminal.dto.TerminalDto;
import kz.capitalpay.server.terminal.mapper.TerminalMapper;
import kz.capitalpay.server.terminal.model.Terminal;
import kz.capitalpay.server.terminal.repository.TerminalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static kz.capitalpay.server.constants.ErrorDictionary.TERMINAL_NOT_FOUND;

@Service
public class TerminalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalService.class);
    private final TerminalRepository terminalRepository;
    private final TerminalMapper terminalMapper;

    public TerminalService(TerminalRepository terminalRepository, TerminalMapper terminalMapper) {
        this.terminalRepository = terminalRepository;
        this.terminalMapper = terminalMapper;
    }

    public ResultDTO saveTerminal(TerminalDto dto) {
        Terminal terminal = terminalMapper.toTerminal(dto);
        terminal = terminalRepository.save(terminal);
        return new ResultDTO(true, terminal, 0);
    }

    public ResultDTO deleteTerminal(IdDto dto) {
        Terminal terminal = terminalRepository.findByIdAndDeletedFalse(dto.getId()).orElse(null);
        if (Objects.isNull(terminal)) {
            return TERMINAL_NOT_FOUND;
        }
        if (!terminal.isFree()) {
            return ErrorDictionary.OCCUPIED_TERMINAL;
        }
        terminal.setDeleted(true);
        terminalRepository.save(terminal);
        return new ResultDTO(true, true, 0);
    }

    public ResultDTO findTerminalById(Long id) {
        Terminal terminal = terminalRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (Objects.isNull(terminal)) {
            return TERMINAL_NOT_FOUND;
        }
        return new ResultDTO(true, terminal, 0);
    }

    public ResultDTO findAll() {
        List<Terminal> terminals = terminalRepository.findAllByDeletedFalse();
        return new ResultDTO(true, terminals, 0);
    }

    public ResultDTO findFreeTerminals() {
        List<Terminal> terminals = terminalRepository.findAllByDeletedFalseAndFreeTrue();
        return new ResultDTO(true, terminals, 0);
    }


}
