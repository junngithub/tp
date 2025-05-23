package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.modifications.Modification;
import seedu.address.model.person.exceptions.CannotRedoException;

/**
 * Restores the state of the address book before the last command that modifies it.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";

    public static final String MESSAGE_SUCCESS = "The following change has been restored: \n%s";
    public static final String MESSAGE_SUCCESS_MULTIPLE =
            "The last %d undone change(s) have been restored! (Requested: %d changes)";
    public static final String MESSAGE_CANNOT_REDO = "There are no more changes to restore!";
    public static final String MESSAGE_NOT_POSITIVE = "Number of changes to restore must be a positive integer!";
    public static final String MESSAGE_LIMIT_EXCEEDED = "Number of changes to restore must not exceed 100000!";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Restores the last undone changes to the person list.\n"
        + "Format: " + COMMAND_WORD + " [NUMBER_OF_CHANGES]\n"
        + "Parameters: NUMBER_OF_CHANGES (must be a positive integer)\n"
        + "Example: " + COMMAND_WORD + " 3\n"
        + "NUMBER_OF_CHANGES must not exceed 100000.\n"
        + "If NUMBER_OF_CHANGES is not specified, it defaults to 1.";

    private final int numberOfTimes;

    public RedoCommand() {
        this.numberOfTimes = 1;
    }

    public RedoCommand(int numberOfTimes) {
        this.numberOfTimes = numberOfTimes;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert(this.numberOfTimes >= 1 && this.numberOfTimes <= 100000);

        if (this.numberOfTimes == 1) {
            Modification restoredMod;
            try {
                restoredMod = model.redoAddressBook();
            } catch (CannotRedoException e) {
                throw new CommandException(MESSAGE_CANNOT_REDO);
            }
            return new CommandResult(
                String.format(MESSAGE_SUCCESS, restoredMod.getUserDescription()));
        } else {
            List<Modification> restoredMods = model.redoAddressBookMultiple(this.numberOfTimes);
            if (restoredMods.size() == 0) {
                throw new CommandException(MESSAGE_CANNOT_REDO);
            } else {
                return new CommandResult(
                    String.format(MESSAGE_SUCCESS_MULTIPLE, restoredMods.size(), this.numberOfTimes));
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RedoCommand)) {
            return false;
        }
        RedoCommand otherCommand = (RedoCommand) other;
        return numberOfTimes == (otherCommand.numberOfTimes);
    }
}
