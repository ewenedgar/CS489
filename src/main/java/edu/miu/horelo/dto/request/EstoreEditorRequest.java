package edu.miu.horelo.dto.request;

public record EstoreEditorRequest(
        String phoneNumber,
        String name,
        String email,
        String logo,
        UserRequest editor
) {
}
