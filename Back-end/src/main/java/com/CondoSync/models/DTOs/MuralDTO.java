package com.CondoSync.models.DTOs;

import com.CondoSync.models.Image;
import com.CondoSync.models.Mural;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MuralDTO {

  private Integer id;
  @NotBlank(message = "O título é obrigatório")
  @Size(max = 50, message = "O título deve ter menos de 50 caracteres")
  private String title;
  @Size(max = 255, message = "A descrição deve ter menos de 255 caracteres")
  private String description;
  private boolean status;

  private ImageMuralDTO image;

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  public class ImageMuralDTO {

    private Integer id;
    @NotBlank
    private String base64;
    private String name;
    private Boolean defaultImg;

    @Override
    public String toString() {
      return "ImageMuralDTO [id=" + id + ", base64=" + base64 + ", name=" + name + ", defaultImg=" + defaultImg + "]";
    }

  }

  public MuralDTO(Mural mural) {
    this.id = mural.getId();
    this.title = mural.getTitle();
    this.description = mural.getDescription();
    this.status = mural.isStatus();
    this.image = new ImageMuralDTO();
    this.image.setId(mural.getImage().getId());
    this.image.setBase64(mural.getImage().getBase64());
    this.image.setName(mural.getImage().getName());
    this.image.setDefaultImg(mural.getImage().getDefaultImg());

  }

  @Override
  public String toString() {
    return "MuralDTO [id=" + id + ", title=" + title + ", description=" + description + ", status=" + status
        + ", image=" + image + "]";
  }

}
