package nl.sense_os.iris_android.task.store;

/**
 * Created by panjiyudasetya on 12/28/16.
 */

public interface ErrorListener {
    void unexpectedType(String description);

    interface DeletionTaskErrorListener {
        void deletingNonExistingTask();
    }

    interface DeletionActionErrorListener {
        void deletingUnavailableAction();
    }

    interface DeletionInputErrorListener {
        void deletingUnavailableInput();
    }

    interface AdditionErrorListener {
        void addingTaskWithUnavailableInput(String inputName);
    }
}
