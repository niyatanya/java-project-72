@import hexlet.code.dto.UrlsPage
@import java.time.format.DateTimeFormatter
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <section>

        <div class="container-lg mt-5">
            <h1>Сайты</h1>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th>Имя</th>
                    <th class="col-2">Последняя проверка</th>
                    <th class="col-1">Код ответа</th>
                </tr>
                </thead>
                <tbody>

                @for(var url : page.getUrls())
                    <tr>
                        <td>
                            ${url.getId()}
                        </td>
                        <td>
                            <a href="/urls/${url.getId()}">${url.getName()}</a>
                        </td>
                        <td>
                            @if(url.getUrlChecks() != null && !url.getUrlChecks().isEmpty())
                            ${url.getUrlChecks().getFirst().getCreatedAt()
                            .toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm"))}
                            @endif
                        </td>
                        <td>
                            @if(url.getUrlChecks() != null && !url.getUrlChecks().isEmpty())
                            ${String.valueOf(url.getUrlChecks().getFirst().getStatusCode())}
                            @endif
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>

    </section>
`
)