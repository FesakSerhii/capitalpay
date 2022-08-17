package kz.capitalpay.server.terminal.mapper;

import kz.capitalpay.server.terminal.dto.TerminalDto;
import kz.capitalpay.server.terminal.model.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TerminalMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalMapper.class);

    public Terminal toTerminal(TerminalDto dto) {
        Terminal terminal = new Terminal();
        terminal.setInputTerminalId(dto.getInputTerminalId());
        terminal.setOutputTerminalId(dto.getOutputTerminalId());
        terminal.setName(dto.getName());
        terminal.setId(dto.getId());
        return terminal;
    }
}
