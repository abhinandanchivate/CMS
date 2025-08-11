# Optimizely CMS POC (single, end-to-end demo)

Below is a **single POC** that touches everything in your screenshot: CMS basics, blocks/pages, property types & validators, visitor groups, media, scheduled jobs, dynamic data store, caching, multilingual, search, deployment (on-prem & DXP notes), **plus headless with Next.js** (and Optimizely Graph/Content Delivery API).

---

## Scope (what you’ll demo in \~60–90 mins)

* MVC site on **Optimizely CMS 12 (.NET 8)** with:

  * Page types: `HomePage`, `ArticlePage`
  * Blocks: `HeroBlock`, `RichTextBlock`, `PromoBlock`
  * Header/Footer (component-based layout), navigation, media
  * Property types & custom validator
  * Visitor group–gated banner
  * **Scheduled job** + **Dynamic Data Store (DDS)**
  * **Caching** (response + object cache)
  * **Multilingual** (EN/HI) with culture-specific properties
  * **Search**: Optimizely Search & Navigation *or* fallback simple search
* **Headless**: Next.js app reading the same content via **Content Delivery API**
  (and optional Optimizely Graph, if enabled)

---

## Architecture (hybrid)

* **Backend** (ASP.NET Core): Optimizely CMS 12 app
* **Frontend A (MVC)**: Razor views for editors (WYSIWYG-like)
* **Frontend B (Headless)**: Next.js 14 (App Router) consuming CMS API
* **Storage**: SQL Server (LocalDB / Docker)
* **Search**: Optimizely Search & Navigation (Find) *or* simple repo search
* **Deployment**: Local Docker; notes for DXP

---

## Repo structure

```
/optly-poc/
  backend/
    src/
      Optly.Poc.Web/
        Controllers/
        Features/
          Blocks/
            HeroBlock.cs
            RichTextBlock.cs
            PromoBlock.cs
          Pages/
            HomePage.cs
            ArticlePage.cs
          Media/
            ImageFile.cs
          Navigation/
            NavigationService.cs
          Visitors/
            TimeOfDayCriterion.cs (optional custom)
          Jobs/
            SampleCleanupJob.cs
          Dds/
            LeadCapture.cs
        Views/
          Shared/_Layout.cshtml
          HomePage/Index.cshtml
          ArticlePage/Index.cshtml
          Blocks/HeroBlock.cshtml
          Blocks/RichTextBlock.cshtml
          Blocks/PromoBlock.cshtml
        appsettings.json
        Program.cs
    tests/ (optional)
  headless/
    next/
      app/[...slug]/page.tsx
      app/layout.tsx
      lib/cms.ts
      .env.local
  docker/
    docker-compose.yml
    mssql.env
  README.md
```

---

## Backend setup (quick start)

1. **Create CMS 12 project**
   Use Optimizely CMS templates or start from empty ASP.NET Core + add packages:

* `EPiServer.CMS` / `EPiServer.CMS.AspNetCore`
* `EPiServer.CMS.UI` (for /EPiServer edit/admin)
* `EPiServer.ContentDeliveryApi.Cms` (for headless)
* `EPiServer.Find.Cms` (if using Search & Navigation)

2. **Program.cs (minimal hosting)**

```csharp
using EPiServer.Cms;
using EPiServer.Cms.Shell;
using EPiServer.ContentApi.Cms;
using EPiServer.Web.Routing;
using EPiServer.Find.Cms; // if using Find
var builder = WebApplication.CreateBuilder(args);

builder.Services
    .AddCms()
    .AddCmsAspNetCore()
    .AddContentApiCms()
    .AddFind(); // optional

builder.Services.AddRazorPages();
builder.Services.AddControllersWithViews();

var app = builder.Build();

app.UseStaticFiles();
app.UseRouting();
app.UseAuthentication();
app.UseAuthorization();
app.UseEndpoints(endpoints =>
{
    endpoints.MapControllerRoute("Default", "{controller=Home}/{action=Index}/{id?}");
    endpoints.MapRazorPages();
    endpoints.MapContent();
    endpoints.MapControllers();
});

app.Run();
```

3. **Connection string** in `appsettings.json` to SQL Server/LocalDB.

---

## Core content types & components

### Page types

```csharp
// Features/Pages/HomePage.cs
using EPiServer.Core;
using EPiServer.DataAnnotations;
using EPiServer.Web;

[ContentType(DisplayName = "Home Page", GUID = "11111111-1111-1111-1111-111111111111")]
public class HomePage : PageData
{
    [CultureSpecific]
    [Display(Name = "Main Content", GroupName = SystemTabNames.Content, Order = 10)]
    public virtual ContentArea MainContentArea { get; set; }

    [Display(Name = "Hero", GroupName = SystemTabNames.Content, Order = 5)]
    public virtual ContentReference HeroBlock { get; set; }
}

// Features/Pages/ArticlePage.cs
[ContentType(DisplayName = "Article Page", GUID = "22222222-2222-2222-2222-222222222222")]
public class ArticlePage : PageData
{
    [CultureSpecific]
    [UIHint(UIHint.Textarea)]
    [Display(Name = "Body", Order = 10)]
    public virtual XhtmlString Body { get; set; }

    [Display(Name = "Hero Image", Order = 5)]
    public virtual ContentReference HeroImage { get; set; }
}
```

### Blocks

```csharp
// Features/Blocks/HeroBlock.cs
[ContentType(DisplayName = "Hero Block", GUID = "33333333-3333-3333-3333-333333333333")]
public class HeroBlock : BlockData
{
    [Display(Name = "Heading", Order = 5)]
    [Required] // validator example
    public virtual string Heading { get; set; }

    [Display(Name = "Subheading", Order = 10)]
    public virtual string Subheading { get; set; }

    [Display(Name = "Background Image", Order = 20)]
    public virtual ContentReference BackgroundImage { get; set; }
}

// Features/Blocks/RichTextBlock.cs
[ContentType(DisplayName = "Rich Text Block", GUID = "44444444-4444-4444-4444-444444444444")]
public class RichTextBlock : BlockData
{
    [CultureSpecific]
    [UIHint(UIHint.Textarea)]
    [Display(Name = "Content", Order = 5)]
    public virtual XhtmlString Content { get; set; }
}
```

### Custom validation (IValidate)

```csharp
using EPiServer.Validation;

public class HeroBlockValidator : IValidate<HeroBlock>
{
    public IEnumerable<ValidationError> Validate(HeroBlock block)
    {
        if (string.IsNullOrWhiteSpace(block.Heading))
        {
            yield return new ValidationError
            {
                ErrorMessage = "Heading is required.",
                Severity = ValidationErrorSeverity.Error,
                PropertyName = nameof(block.Heading)
            };
        }
    }
}
```

### Media type

```csharp
// Features/Media/ImageFile.cs
[ContentType(GUID = "55555555-5555-5555-5555-555555555555")]
[MediaDescriptor(ExtensionString = "jpg,jpeg,png,webp")]
public class ImageFile : ImageData
{
    public virtual string Photographer { get; set; }
}
```

### Views (samples)

```cshtml
@* Views/Blocks/HeroBlock.cshtml *@
@model Features.Blocks.HeroBlock
<section class="hero" style="background-image:url('@Url.ContentUrl(Model.BackgroundImage)')">
  <h1>@Model.Heading</h1>
  <p>@Model.Subheading</p>
</section>

@* Views/HomePage/Index.cshtml *@
@model Features.Pages.HomePage
@Html.Partial("~/Views/Blocks/HeroBlock.cshtml", Model.ContentLoader().Get<HeroBlock>(Model.HeroBlock))
@Html.PropertyFor(x => x.MainContentArea)  @* renders blocks dropped by editors *@
```

> `ContentLoader()` above implies you inject `IContentLoader` in a base helper/service; feel free to render by `Html.ContentLink` or fetch via a controller.

---

## Navigation & layout

```csharp
// Features/Navigation/NavigationService.cs
public class NavigationService
{
    private readonly IContentLoader _loader;
    public NavigationService(IContentLoader loader) => _loader = loader;

    public IEnumerable<PageData> TopMenu(PageReference start)
        => _loader.GetChildren<PageData>(start).Where(p => p.VisibleInMenu);
}
```

```cshtml
@* Views/Shared/_Layout.cshtml *@
@inject Features.Navigation.NavigationService Nav
@{
    var startPage = EPiServer.Web.SiteDefinition.Current.StartPage;
    var items = Nav.TopMenu(startPage);
}
<nav>
  <ul>
    @foreach (var p in items) {
      <li><a href="@Url.ContentUrl(p.ContentLink)">@p.Name</a></li>
    }
  </ul>
</nav>
@RenderBody()
<footer>© Optimizely POC</footer>
```

---

## Visitor groups (rule-based rendering)

* In Admin → Visitor Groups, create a group “MorningVisitors” (Time is 6–12).
* In your view, show a promo only to that group:

```cshtml
@using EPiServer.Personalization.VisitorGroups
@inject IVisitorGroupRoleRepository VisitorGroups

@if (VisitorGroups.IsPrincipalInGroup(User, "MorningVisitors")) {
  <div class="alert alert-info">Good morning! Special promo for you.</div>
}
```

*(You can also build a custom criterion; keep built-in for the POC.)*

---

## Scheduled Job

```csharp
// Features/Jobs/SampleCleanupJob.cs
using EPiServer.Scheduler;
using EPiServer.PlugIn;

[ScheduledPlugIn(DisplayName = "Sample Cleanup Job")]
public class SampleCleanupJob : ScheduledJobBase
{
    public override string Execute()
    {
        // do cleanup, export, etc.
        return "Cleanup completed at " + DateTime.Now;
    }
}
```

Runs from **/EPiServer → Admin → Scheduled Jobs**.

---

## Dynamic Data Store (DDS)

```csharp
// Features/Dds/LeadCapture.cs
using EPiServer.Data.Dynamic;
using EPiServer.DataAnnotations;

[EPiServerDataStore(AutomaticallyCreateStore = true, StoreName = "LeadCaptureStore")]
public class LeadCapture
{
    public Identity Id { get; set; }
    public DateTime Created { get; set; } = DateTime.UtcNow;
    public string Email { get; set; }
    public string Source { get; set; }
}

// usage
var store = DynamicDataStoreFactory.Instance.CreateStore(typeof(LeadCapture));
store.Save(new LeadCapture { Email = "a@b.com", Source = "footer-form" });
```

---

## Caching

**Page response cache (public pages):**

```csharp
[ResponseCache(Duration = 120, Location = ResponseCacheLocation.Any, NoStore = false)]
public class ArticleController : PageController<ArticlePage> { /* ... */ }
```

**Object cache (reusable data):**

```csharp
using EPiServer.Framework.Cache;
public class WeatherService
{
    private readonly ISynchronizedObjectInstanceCache _cache;
    public WeatherService(ISynchronizedObjectInstanceCache cache) => _cache = cache;

    public string GetToday() =>
        _cache.ReadThrough("weather:today", TimeSpan.FromMinutes(30), () => "Cloudy");
}
```

---

## Multilingual (EN/HI)

* Enable languages in Admin → Manage Website Languages
* Mark properties `[CultureSpecific]` (already used above)
* Editors translate the same page to `hi-IN`; views pick localized values automatically.

---

## Search

**Option A: Optimizely Search & Navigation (Find)**

```csharp
using EPiServer.Find;
using EPiServer.Find.Cms;
public class SearchService
{
  private readonly IClient _client;
  public SearchService(IClient client) => _client = client;

  public IEnumerable<IContent> Search(string q) =>
    _client.UnifiedSearchFor(q).GetResult().Select(r => r.OriginalObject as IContent);
}
```

**Option B: Simple repo search (fallback)**

```csharp
public IEnumerable<PageData> SimpleSearch(string q)
{
  var roots = _loader.GetChildren<PageData>(ContentReference.StartPage);
  return roots.SelectMany(r => _loader.GetDescendents(r.ContentLink))
    .Select(cl => _loader.Get<IContent>(cl) as PageData)
    .Where(p => p != null && (p.Name.Contains(q, StringComparison.OrdinalIgnoreCase)
      || (p is ArticlePage ap && ap.Body?.ToHtmlString().Contains(q, StringComparison.OrdinalIgnoreCase) == true)));
}
```

---

## Headless (Content Delivery API) + Next.js

**Backend**: ensure `AddContentApiCms()` is configured (above).
You can fetch by URL or ID: `/api/episerver/v3/content/?url=/news/my-article&expand=*`

**Next.js (App Router)**

```ts
// headless/next/lib/cms.ts
export async function fetchByUrl(urlPath: string) {
  const res = await fetch(
    `${process.env.CMS_BASE_URL}/api/episerver/v3/content/?url=${encodeURIComponent(urlPath)}&expand=*`,
    { next: { revalidate: 60 } }
  );
  if (!res.ok) throw new Error("CMS fetch failed");
  return res.json();
}
```

```tsx
// headless/next/app/[...slug]/page.tsx
import { fetchByUrl } from "@/lib/cms";

export default async function Page({ params }: { params: { slug?: string[] } }) {
  const path = "/" + (params.slug?.join("/") ?? "");
  const data = await fetchByUrl(path);

  // render based on content type (ArticlePage, HomePage, etc.)
  const item = data?.results?.[0];
  if (!item) return <div>Not found</div>;

  if (item.contentType?.includes("ArticlePage")) {
    return (
      <main>
        <h1>{item.name}</h1>
        <div dangerouslySetInnerHTML={{ __html: item.body?.value ?? "" }} />
      </main>
    );
  }

  return <main><h1>{item.name}</h1></main>;
}
```

**Optimizely Graph (optional)**: if enabled, call GraphQL endpoint with a token and query content by type—use the same URL mapping in Next.js.

---

## Sample component-based “mini site”

* **Header**: uses `NavigationService` for top menu
* **Home**: editors drop `HeroBlock`, `PromoBlock`, `RichTextBlock` into `MainContentArea`
* **Article**: authored content + hero image
* **Footer form**: saves to DDS `LeadCapture`
* **Visitor group**: morning banner
* **Search page**: uses Find or fallback search
* **Scheduled job**: reports count of DDS leads weekly

---

## Running locally

**SQL via Docker**

```yaml
# docker/docker-compose.yml
services:
  mssql:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      - ACCEPT_EULA=Y
      - SA_PASSWORD=Your_password123
    ports: ["1433:1433"]
```

**Backend**

```bash
cd backend/src/Optly.Poc.Web
dotnet restore
dotnet run
# CMS UI at http://localhost:5000/EPiServer
```

**Next.js**

```bash
cd headless/next
cp .env.local.example .env.local
# CMS_BASE_URL=http://localhost:5000
npm i
npm run dev
# http://localhost:3000
```

---


